package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

import static com.novoda.dungeoncrawler.Direction.LEFT_TO_RIGHT;

class GameEngine {

    private static final Store<Redux.GameState> store = Store.create(new Redux.GameReducer(), Redux.GameState.getInitialState());

    // GAME
    private static final int MIN_REDRAW_INTERVAL = 33;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps
    private static final int TIMEOUT = 30000;
    private static final int LEVEL_COUNT = 9;
    private static final boolean USE_GRAVITY = true;     // 0/1 use gravity (LED strip going up wall)
    private static final Direction DIRECTION = Direction.LEFT_TO_RIGHT;
    private static final int START_LEVEL = 0;


    // WOBBLE ATTACK
    private static final int ATTACK_DURATION = 700;    // Duration of a wobble attack (ms)
    private static final int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    private static final int BOSS_WIDTH = 40;
    private static final int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack // TODO DOESN'T BELONG HERE

    // PLAYER
    private static final int TOTAL_LIVES = 3;
    private static final int MAX_PLAYER_SPEED = 15;     // Max move speed of the player

    // Hooks
    private final AttackMonitor attackMonitor;
    private final KillMonitor killMonitor;
    private final MovementMonitor movementMonitor;
    private final DeathMonitor deathMonitor;
    private final WinMonitor winMonitor;
    private final NoInputMonitor noInputMonitor;
    private final CompleteMonitor completeMonitor;
    private final GameOverMonitor gameOverMonitor;
    private final DrawCallback drawCallback;
    private final JoystickActuator inputActuator;

    private JoystickActuator.JoyState joyState;

    GameEngine(AttackMonitor attackMonitor, KillMonitor killMonitor, MovementMonitor movementMonitor, DeathMonitor deathMonitor, WinMonitor winMonitor, NoInputMonitor noInputMonitor, CompleteMonitor completeMonitor, GameOverMonitor gameOverMonitor, DrawCallback drawCallback, JoystickActuator inputActuator) {
        this.attackMonitor = attackMonitor;
        this.killMonitor = killMonitor;
        this.movementMonitor = movementMonitor;
        this.deathMonitor = deathMonitor;
        this.winMonitor = winMonitor;
        this.noInputMonitor = noInputMonitor;
        this.completeMonitor = completeMonitor;
        this.gameOverMonitor = gameOverMonitor;
        this.drawCallback = drawCallback;
        this.inputActuator = inputActuator;
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
        store.getState().clock.start();
        store.dispatch(Redux.GameActions.nextLevel());
        drawCallback.drawLives(store.getState().lives);
    }

    private void spawnEnemy(int pos, int dir, int speed, int wobble) {
        int playerSide = pos > store.getState().playerPosition ? 1 : -1;
        store.getState().enemyPool.add(new Enemy(pos, dir, speed, wobble, playerSide));
    }

    private void spawnEnemySpawner(int position, int rate, int speed, int direction, int activate, long millis) {
        store.getState().enemySpawnerPool.add(new EnemySpawner(position, rate, speed, direction, activate, millis));
    }

    private void moveBoss() {
        Boss boss = store.getState().boss;
        StartClock clock = store.getState().clock;
        int spawnSpeed = boss.getSpeed();
        store.getState().enemySpawnerPool.clear();
        spawnEnemySpawner(boss.getPosition(), spawnSpeed, 3, 0, 0, clock.millis());
        spawnEnemySpawner(boss.getPosition(), spawnSpeed, 3, 1, 0, clock.millis());
    }

    void loop() {
//        Log.d("TUT", "loop");
        long frameTime = store.getState().clock.millis();

        if (store.getState().stage == Stage.PLAY) {
            if (store.getState().attacking) {
                attackMonitor.onAttack();
            } else {
                movementMonitor.onMove(joyState == null ? 0 : joyState.tilt);
            }
        } else if (store.getState().stage == Stage.DEAD) {
            deathMonitor.onDeath();
        }

        joyState = inputActuator.getInput();
        if (frameTime - store.getState().previousFrameTime >= MIN_REDRAW_INTERVAL) {
//            int joyTilt = Math.abs(joyState.tilt);
//            store.dispatch(Redux.GameActions.nextFrame(joyTilt));
            
            store.getState().previousFrameTime = frameTime;

            if (Math.abs(joyState.tilt) > JoystickActuator.DEADZONE) {
                store.getState().lastInputTime = frameTime;
                if (store.getState().stage == Stage.SCREENSAVER) {
                    store.getState().levelNumber = START_LEVEL - 1;
                    store.getState().stageStartTime = frameTime;
                    store.getState().stage = Stage.LEVEL_COMPLETE;
                }
            } else {
                if (store.getState().lastInputTime + TIMEOUT < frameTime) {
                    store.getState().stage = Stage.SCREENSAVER;
                }
            }
            if (store.getState().stage == Stage.SCREENSAVER) {
                noInputMonitor.onNoInput(frameTime);
            } else if (store.getState().stage == Stage.PLAY) {
                // PLAYING
                if (store.getState().attacking && store.getState().attackMillis + ATTACK_DURATION < frameTime) {
                    store.getState().attacking = false;
                }

                // If not attacking, check if they should be
                if (!store.getState().attacking && joyState.wobble > ATTACK_THRESHOLD) {
                    store.getState().attackMillis = frameTime;
                    store.getState().attacking = true;
                }

                // If still not attacking, move!
                store.getState().playerPosition += store.getState().playerPositionModifier;
                if (!store.getState().attacking) {
                    int moveAmount = (int) (joyState.tilt / 6.0);
                    if (DIRECTION == LEFT_TO_RIGHT) {
                        moveAmount = -moveAmount;
                    }
                    moveAmount = constrain(moveAmount, -MAX_PLAYER_SPEED, MAX_PLAYER_SPEED);
                    store.getState().playerPosition -= moveAmount;
                    if (store.getState().playerPosition < 0) {
                        store.getState().playerPosition = 0;
                    }
                    if (store.getState().playerPosition >= 1000 && !store.getState().boss.isAlive()) {
                        // Reached exit!
                        levelComplete();
                        return;
                    }
                }

                if (inLava(store.getState().playerPosition)) {
                    die();
                }

                // Ticks and draw calls
                tickConveyors();
                tickEnemySpawners();
                tickBoss();
                tickLava();
                tickEnemies();
                playingStateDraw();

            } else if (store.getState().stage == Stage.DEAD) {
                // DEAD
                drawCallback.startDraw();

                tickParticles();
                drawParticles();

                if (areAllParticlesDeactive()) {
                    // used to be loadLevel()
                    store.dispatch(Redux.GameActions.restartLevel());
                }
            } else if (store.getState().stage == Stage.LEVEL_COMPLETE) {
                if (frameTime < store.getState().stageStartTime + 1200) {
                    winMonitor.onWin(store.getState().stageStartTime, frameTime);
                } else {
                    nextLevel();
                }
            } else if (store.getState().stage == Stage.GAME_COMPLETE) {
                if (frameTime < store.getState().stageStartTime + 5500) {
                    completeMonitor.onGameComplete(store.getState().stageStartTime, frameTime);
                } else {
                    nextLevel();
                }
            } else if (store.getState().stage == Stage.GAME_OVER) {
                store.getState().stageStartTime = 0;
                gameOverMonitor.onGameOver();
            }

            drawCallback.finishDraw();
        }
    }

    private void playingStateDraw() {
        drawCallback.startDraw();
        long frame = 10000 + store.getState().clock.millis();
        for (Conveyor conveyor : store.getState().conveyorPool) {
            drawCallback.drawConveyor(conveyor.getStartPoint(), conveyor.getEndPoint(), conveyor.getDirection(), frame);
        }
        for (Enemy enemy : store.getState().enemyPool) { // TODO this is now after the check if the enemy has hit the player - just sanity test this doesn't cause weird state
            if (enemy.isAlive()) {
                drawCallback.drawEnemy(enemy.getPosition());
            }
        }
        for (Lava lava : store.getState().lavaPool) {
            drawCallback.drawLava(lava.getLeft(), lava.getRight(), lava.isEnabled());
        }
        if (store.getState().boss.isAlive()) {
            int bossStartPosition = store.getState().boss.getPosition() - BOSS_WIDTH / 2;
            int bossEndPosition = store.getState().boss.getPosition() + BOSS_WIDTH / 2;
            drawCallback.drawBoss(bossStartPosition, bossEndPosition);
        }

        drawCallback.drawPlayer(store.getState().playerPosition);
        if (store.getState().attacking) {
            int startPoint = store.getState().playerPosition + (ATTACK_WIDTH / 2);
            int endPoint = store.getState().playerPosition - (ATTACK_WIDTH / 2);
            int attackPower = (int) GameEngine.map((int) (store.getState().clock.millis() - store.getState().attackMillis), 0, ATTACK_DURATION, 100, 5);
            drawCallback.drawAttack(startPoint, store.getState().playerPosition, endPoint, attackPower);
        }
        if (!store.getState().boss.isAlive()) {
            drawCallback.drawExit();
        }
    }

    private int constrain(int value, int lower, int upper) {
        return Math.max(Math.min(value, upper), lower);
    }

    private void levelComplete() {
        store.getState().stageStartTime = store.getState().clock.millis();
        store.getState().stage = Stage.LEVEL_COMPLETE;
        if (store.getState().levelNumber == LEVEL_COUNT) {
            store.getState().stage = Stage.GAME_COMPLETE;
        }
        store.getState().lives = TOTAL_LIVES;
        drawCallback.drawLives(store.getState().lives);
    }

    /**
     * Returns if the player is in active lava
     */
    private boolean inLava(int pos) {
        for (Lava lava : store.getState().lavaPool) {
            if (lava.consumes(pos)) {
                return true;
            }
        }
        return false;
    }

    private void die() {
        if (store.getState().levelNumber > 0) {
            store.getState().lives--;
        }
        drawCallback.drawLives(store.getState().lives);
        if (store.getState().lives == 0) {
            store.getState().levelNumber = START_LEVEL;
            store.getState().lives = TOTAL_LIVES;
        }
        for (Particle particle : store.getState().particlePool) {
            particle.spawn(store.getState().playerPosition);
        }
        store.getState().stageStartTime = store.getState().clock.millis();
        store.getState().stage = Stage.DEAD;
    }

    private void tickConveyors() {
        store.getState().playerPositionModifier = 0;
        for (Conveyor conveyor : store.getState().conveyorPool) {
            store.getState().playerPositionModifier = conveyor.affect(store.getState().playerPosition);
        }
    }

    private void tickEnemySpawners() {
        long mm = store.getState().clock.millis();
        for (EnemySpawner enemySpawner : store.getState().enemySpawnerPool) {
            if (enemySpawner.shouldSpawn(mm)) {
                EnemySpawner.Spawn spawn = enemySpawner.spawn(mm);
                spawnEnemy(spawn.getPosition(), spawn.getDirection(), spawn.getSpeed(), 0);
            }
        }
    }

    private void tickBoss() {
        if (!store.getState().boss.isAlive()) {
            return;
        }
        int bossStartPosition = store.getState().boss.getPosition() - BOSS_WIDTH / 2;
        int bossEndPosition = store.getState().boss.getPosition() + BOSS_WIDTH / 2;
        // CHECK COLLISION
        if (store.getState().playerPosition > bossStartPosition
            && store.getState().playerPosition < bossEndPosition) {
            die();
            return;
        }
        // CHECK FOR ATTACK
        if (store.getState().attacking) {
            int attackStartPosition = store.getState().playerPosition + (ATTACK_WIDTH / 2);
            int attackEndPosition = store.getState().playerPosition - (ATTACK_WIDTH / 2);
            if ((attackStartPosition >= bossStartPosition
                && attackStartPosition <= bossEndPosition)
                || attackEndPosition <= bossEndPosition
                && attackEndPosition >= bossStartPosition) {
                store.getState().boss.hit();
                if (store.getState().boss.isAlive()) {
                    moveBoss();
                } else {
                    for (EnemySpawner enemySpawner : store.getState().enemySpawnerPool) {
                        enemySpawner.kill();
                    }
                }
            }
        }
    }

    private void tickLava() {
        long mm = store.getState().clock.millis();
        for (Lava lava : store.getState().lavaPool) {
            lava.toggleLava(mm);
        }
    }

    private void tickEnemies() {
        for (Enemy enemy : store.getState().enemyPool) {
            if (enemy.isAlive()) {
                enemy.tick(store.getState().clock.millis());
                // hit attack?
                if (store.getState().attacking) {
                    int attackStartPosition = store.getState().playerPosition - (ATTACK_WIDTH / 2);
                    int attackEndPosition = store.getState().playerPosition + (ATTACK_WIDTH / 2);
                    if (enemy.hitAttack(attackStartPosition, attackEndPosition)) {
                        enemy.kill();
                        killMonitor.onKill();
                    }
                }
                if (inLava(enemy.getPosition())) {
                    enemy.kill();
                    killMonitor.onKill();
                }
                // hit player?
                if (enemy.hitPlayer(store.getState().playerPosition)) {
                    die();
                    return;
                }
            }
        }
    }

    private void tickParticles() {
        for (Particle particle : store.getState().particlePool) {
            if (particle.isAlive()) {
                particle.tick(USE_GRAVITY);
            }
        }
    }

    private void drawParticles() {
        for (Particle particle : store.getState().particlePool) {
            if (particle.isAlive()) {
                drawCallback.drawParticle(particle.getPosition(), particle.getPower());
            }
        }
    }

    private boolean areAllParticlesDeactive() {
        for (Particle particle : store.getState().particlePool) {
            if (particle.isAlive()) {
                return false;
            }
        }
        return true;
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
