package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

public class FrameObserver {

    private static final int ATTACK_DURATION = 700;    // Duration of a wobble attack (ms)
    private static final int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    private static final int BOSS_WIDTH = 40;

    private DrawCallback drawCallback;

    public FrameObserver(Store<Redux.GameState> store,
                         NoInputMonitor noInputMonitor,
                         WinMonitor winMonitor,
                         CompleteMonitor completeMonitor,
                         GameOverMonitor gameOverMonitor,
                         DrawCallback drawCallback,
                         FrameCallback frameCallback,
                         StartClock clock) {
        this.drawCallback = drawCallback;
        store.subscribe(gameState -> {
            long frameTime = clock.millis();
            frameCallback.onFrameStart();
            drawCallback.drawLives(gameState.lives);
            if (gameState.stage == Stage.SCREENSAVER) {
                noInputMonitor.onNoInput(frameTime);
            } else if (gameState.stage == Stage.PLAY) {
                playingStateDraw(gameState, frameTime);
            } else if (gameState.stage == Stage.PAUSE) {
                playingStateDraw(gameState, frameTime);
            } else if (gameState.stage == Stage.DEAD) {
                deadStateDraw(gameState);
            } else if (gameState.stage == Stage.LEVEL_COMPLETE) {
                if (frameTime < gameState.stageStartTime + 1200) {
                    winMonitor.onWin(gameState.stageStartTime, frameTime);
                }
            } else if (gameState.stage == Stage.GAME_COMPLETE) {
                if (frameTime < gameState.stageStartTime + 5500) {
                    completeMonitor.onGameComplete(gameState.stageStartTime, frameTime);
                }
            } else if (gameState.stage == Stage.GAME_OVER) {
                gameOverMonitor.onGameOver();
            }
            frameCallback.onFrameEnd();
        });
    }

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
            int attackPower = (int) GameEngine.map((int) (frameTime - newGameState.attackStartedTime), 0, ATTACK_DURATION, 100, 5);
            drawCallback.drawAttack(startPoint, playerPosition, endPoint, attackPower);
        }
    }

    private void deadStateDraw(Redux.GameState gameState) {
        for (Particle particle : gameState.particlePool) {
            if (particle.isAlive()) {
                drawCallback.drawParticle(particle.getPosition(), particle.getPower());
            }
        }
    }

    public interface NoInputMonitor {
        void onNoInput(long frameTime);
    }

    public interface WinMonitor {
        void onWin(long levelStartTime, long levelCurrentTime);
    }

    public interface CompleteMonitor {
        void onGameComplete(long levelStartTime, long levelCurrentTime);
    }

    public interface GameOverMonitor {
        void onGameOver();
    }

    public interface FrameCallback {
        void onFrameStart();

        void onFrameEnd();
    }

    public interface DrawCallback {
        void drawPlayer(int position);

        void drawConveyor(int startPoint, int endPoint, Direction direction, long frame);

        void drawAttack(int startPoint, int centerPoint, int endPoint, int attackPower);

        void drawParticle(int position, int power);

        void drawEnemy(int position);

        void drawLava(int lavaStartPosition, int lavaEndPosition, boolean enabled);

        void drawBoss(int startPosition, int endPosition);

        void drawExit();

        void drawLives(int lives);
    }
}
