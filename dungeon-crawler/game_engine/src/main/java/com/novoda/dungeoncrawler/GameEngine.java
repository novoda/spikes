package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

class GameEngine {

    private static final Store<Redux.GameState> store = Store.create(new Redux.GameReducer(), Redux.GameState.getInitialState(), new MiddlewareLogger());

    // GAME
    private static final int MIN_REDRAW_INTERVAL = 33;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps

    // WOBBLE ATTACK
    private static final int ATTACK_DURATION = 700;    // Duration of a wobble attack (ms)
    private static final int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    private static final int BOSS_WIDTH = 40;

    // Hooks
    private final AttackMonitor attackMonitor;
    private final MovementMonitor movementMonitor;
    private final DeathMonitor deathMonitor;
    private final DrawCallback drawCallback;
    private final JoystickActuator inputActuator;

    private JoystickActuator.JoyState joyState;

    GameEngine(AttackMonitor attackMonitor, KillMonitor killMonitor, MovementMonitor movementMonitor, DeathMonitor deathMonitor, WinMonitor winMonitor, NoInputMonitor noInputMonitor, CompleteMonitor completeMonitor, GameOverMonitor gameOverMonitor, DrawCallback drawCallback, JoystickActuator inputActuator) {
        this.attackMonitor = attackMonitor;
        this.movementMonitor = movementMonitor;
        this.deathMonitor = deathMonitor;
        this.drawCallback = drawCallback;
        this.inputActuator = inputActuator;

        store.subscribe(gameState -> {
            long frameTime = gameState.clock.millis();
            if (gameState.stage == Stage.GAME_OVER) {
                gameOverMonitor.onGameOver();
            } else if (gameState.stage == Stage.LEVEL_COMPLETE) {
                drawCallback.drawLives(gameState.lives);
            } else if (gameState.stage == Stage.DEAD) {
                drawCallback.drawLives(gameState.lives);
            }

            drawCallback.startDraw();
            if (gameState.stage == Stage.SCREENSAVER) {
                noInputMonitor.onNoInput(frameTime);
            } else if (gameState.stage == Stage.PLAY) {
                playingStateDraw(gameState, frameTime);
            } else if (gameState.stage == Stage.DEAD) {
                deadStateDraw(gameState);
            } else if (gameState.stage == Stage.LEVEL_COMPLETE) {
                if (frameTime < gameState.stageStartTime + 1200) {
                    winMonitor.onWin(gameState.stageStartTime, frameTime);
                } else {
                    nextLevel();
                }
            } else if (gameState.stage == Stage.LEVEL_COMPLETE) {
                if (frameTime < gameState.stageStartTime + 5500) {
                    completeMonitor.onGameComplete(gameState.stageStartTime, frameTime);
                } else {
                    nextLevel();
                }
            }
            drawCallback.finishDraw();
        });
    }

    // TODO I think all of the drawing callbacks need to be
    // outside of the state changing, aka an observer on state change
    private void playingStateDraw(Redux.GameState newGameState, long frameTime) {
        long frame = 10000 + frameTime;
        for (Conveyor conveyor : newGameState.conveyorPool) {
            drawCallback.drawConveyor(conveyor.getStartPoint(), conveyor.getEndPoint(), conveyor.getDirection(), frame);
        }
        for (Enemy enemy : newGameState.enemyPool) {
            if (enemy.isAlive()) {
                drawCallback.drawEnemy(enemy.getPosition());
            }
            // TODO could we put the enemy kill sound callbacks here
            // (if not alive callback)
            // except that it would play the sound on every frame
            // rather than only at the time of death :-)
        }
        for (Lava lava : newGameState.lavaPool) {
            drawCallback.drawLava(lava.getLeft(), lava.getRight(), lava.isEnabled());
        }
        Boss boss = newGameState.boss;
        if (boss.isAlive()) {
            int position = boss.getPosition();
            int bossStartPosition = position - BOSS_WIDTH / 2;
            int bossEndPosition = position + BOSS_WIDTH / 2;
            drawCallback.drawBoss(bossStartPosition, bossEndPosition);
        } else {
            drawCallback.drawExit();
        }

        int playerPosition = newGameState.playerPosition;
        drawCallback.drawPlayer(playerPosition);
        if (newGameState.attacking) {
            int startPoint = playerPosition + (ATTACK_WIDTH / 2);
            int endPoint = playerPosition - (ATTACK_WIDTH / 2);
            int attackPower = (int) GameEngine.map((int) (frameTime - newGameState.attackMillis), 0, ATTACK_DURATION, 100, 5);
            drawCallback.drawAttack(startPoint, playerPosition, endPoint, attackPower);
        }
    }

    private void deadStateDraw(Redux.GameState gameState) {
        for (Particle particle : gameState.particlePool) {
            if (particle.isAlive()) {
                drawCallback.drawParticle(particle.getPosition(), particle.getPower());
            }
        }

        if (areAllParticlesDeactive(gameState)) {
            // used to be loadLevel()
            store.dispatch(Redux.GameActions.restartLevel());
        }
    }

    private static boolean areAllParticlesDeactive(Redux.GameState gameState) {
        for (Particle particle : gameState.particlePool) {
            if (particle.isAlive()) {
                return false;
            }
        }
        return true;
    }

    interface AttackMonitor {
        void onAttack();
    }

    interface KillMonitor {
        void onKill();
    }

    interface MovementMonitor {
        void onMove(int velocity);
    }

    interface DeathMonitor {
        void onDeath();
    }

    interface WinMonitor {
        void onWin(long levelStartTime, long levelCurrentTime);
    }

    interface NoInputMonitor {
        void onNoInput(long frameTime);
    }

    interface CompleteMonitor {
        void onGameComplete(long levelStartTime, long levelCurrentTime);
    }

    interface GameOverMonitor {
        void onGameOver();
    }

    interface DrawCallback {
        void startDraw();

        void drawPlayer(int position);

        void drawConveyor(int startPoint, int endPoint, Direction direction, long frame);

        void drawAttack(int startPoint, int centerPoint, int endPoint, int attackPower);

        void drawParticle(int position, int power);

        void drawEnemy(int position);

        void drawLava(int lavaStartPosition, int lavaEndPosition, boolean enabled);

        void drawBoss(int startPosition, int endPosition);

        void drawExit();

        void drawLives(int lives);

        void finishDraw();
    }

    void loadLevel() {
        store.dispatch(Redux.GameActions.nextLevel());
        drawCallback.drawLives(store.getState().lives);
    }

    void loop() {
        Redux.GameState state = store.getState();
        long frameTime = state.clock.millis();

        if (state.stage == Stage.PLAY) {
            if (state.attacking) {
                attackMonitor.onAttack();
            } else {
                movementMonitor.onMove(joyState == null ? 0 : joyState.tilt);
            }
        } else if (state.stage == Stage.DEAD) {
            deathMonitor.onDeath();
        }

        joyState = inputActuator.getInput();
        if (frameTime - state.previousFrameTime >= MIN_REDRAW_INTERVAL) {
            int joyTilt = Math.abs(joyState.tilt);
            int joyWobble = Math.abs(joyState.wobble);
            store.dispatch(Redux.GameActions.nextFrame(joyTilt, joyWobble));
        }
    }

    private void nextLevel() {
        loadLevel();
    }

    static double map(int valueCoord1,
                      int startCoord1, int endCoord1,
                      int startCoord2, int endCoord2) {

        double epsilon = 1e-12;
        if (Math.abs(endCoord1 - startCoord1) < epsilon) {
            throw new ArithmeticException("/ 0");
        }

        double offset = startCoord2;
        float ratio = (float) (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + offset;
    }
}
