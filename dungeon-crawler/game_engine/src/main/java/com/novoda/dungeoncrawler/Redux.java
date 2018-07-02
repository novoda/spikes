package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Reducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.novoda.dungeoncrawler.Direction.LEFT_TO_RIGHT;
import static com.novoda.dungeoncrawler.Direction.RIGHT_TO_LEFT;

public interface Redux {

    class GameState {

        List<Particle> particlePool = new ArrayList<>();
        List<Enemy> enemyPool = new ArrayList<>();
        List<EnemySpawner> enemySpawnerPool = new ArrayList<>();
        List<Lava> lavaPool = new ArrayList<>();
        List<Conveyor> conveyorPool = new ArrayList<>();

        Boss boss = new Boss();
        long frameTime;
        long lastInputTime;

        long attackStartedTime;
        boolean attacking;

        int levelNumber;
        int playerPositionModifier;
        int playerPosition;
        long stageStartTime;
        Stage stage;
        int lives;

        public static GameState getInitialState() {
            GameState gameState = new GameState();
            gameState.levelNumber = -1;
            gameState.lives = 3;
            gameState.stage = Stage.SCREENSAVER;
            gameState.particlePool = new ArrayList<>();
            gameState.enemyPool = new ArrayList<>();
            gameState.enemySpawnerPool = new ArrayList<>();
            gameState.lavaPool = new ArrayList<>();
            gameState.conveyorPool = new ArrayList<>();
            return gameState;
        }
    }

    enum GameActions {
        GOTO_NEXT_LEVEL, RESTART_LEVEL, GOTO_NEXT_FRAME, PAUSE_GAME, RESUME_GAME;

        public static Action nextLevel(long frameTime) {
            return new Action(GOTO_NEXT_LEVEL.toString(), new Object[]{frameTime});
        }

        public static Action restartLevel(long frameTime) {
            return new Action(RESTART_LEVEL.toString(), new Object[]{frameTime});
        }

        public static Action nextFrame(long frameTime, double joyTilt, double joyWobble) {
            return new Action(GOTO_NEXT_FRAME.toString(), new Object[]{frameTime, joyTilt, joyWobble});
        }

        public static Action pauseGame(long frameTime) {
            return new Action(PAUSE_GAME.toString(), new Object[]{frameTime});
        }

        public static Action resumeGame(long frameTime) {
            return new Action(RESUME_GAME.toString(), new Object[]{frameTime});
        }
    }

    class GameReducer implements Reducer<GameState> {

        @Override
        public GameState reduce(GameState gameState, Action action) {
            try {
                GameActions.valueOf(action.type);
            } catch (IllegalArgumentException e) { // hack cos i'm tired
                return GameState.getInitialState();
            }
            long frameTime = action.getValue(0);
            switch (GameActions.valueOf(action.type)) {
                case GOTO_NEXT_LEVEL:
                    return LevelReducer.nextLevel(gameState, frameTime);
                case RESTART_LEVEL:
                    return LevelReducer.loadLevel(gameState, frameTime);
                case GOTO_NEXT_FRAME:
                    double tilt = action.getValue(1);
                    double wobble = action.getValue(2);
                    return nextFrame(gameState, frameTime, tilt, wobble);
                case PAUSE_GAME:
                    if (gameState.stage == Stage.PLAY) {
                        GameState newGameState = copyOf(gameState, frameTime);
                        newGameState.stage = Stage.PAUSE;
                        return newGameState;
                    } else {
                        return gameState;
                    }
                case RESUME_GAME:
                    if (gameState.stage == Stage.PAUSE) {
                        GameState newGameState = copyOf(gameState, frameTime);
                        newGameState.stage = Stage.PLAY;
                        return newGameState;
                    } else {
                        return gameState;
                    }
                default:
                    throw new IllegalStateException(action.type + " is unknown.");
            }
        }

        private static final long SCREENSAVER_TIMEOUT = TimeUnit.SECONDS.toMillis(20);
        private static final int ATTACK_DURATION = 700;
        private static final int ATTACK_THRESHOLD = 30000;
        private static final int MAX_PLAYER_SPEED = 15;     // Max move speed of the player
        private static final int LEVEL_COUNT = 9;
        private static final int TOTAL_LIVES = 3;
        private static final int START_LEVEL = 0;
        private static final int BOSS_WIDTH = 40;
        private static final int ATTACK_WIDTH = 70;
        private static final boolean USE_GRAVITY = true;
        private static final Direction DIRECTION = Direction.LEFT_TO_RIGHT;

        private GameState copyOf(GameState gameState, long frameTime) {
            GameState newGameState = new GameState();
            newGameState.frameTime = frameTime;
            newGameState.stage = gameState.stage;
            newGameState.lives = gameState.lives;
            newGameState.levelNumber = gameState.levelNumber;
            newGameState.stageStartTime = gameState.stageStartTime;
            newGameState.lastInputTime = gameState.lastInputTime;
            newGameState.playerPosition = gameState.playerPosition;
            newGameState.playerPositionModifier = gameState.playerPositionModifier;
            newGameState.attacking = gameState.attacking;
            newGameState.attackStartedTime = gameState.attackStartedTime;
            newGameState.boss = gameState.boss;
            newGameState.particlePool = gameState.particlePool;
            newGameState.enemyPool = gameState.enemyPool;
            newGameState.enemySpawnerPool = gameState.enemySpawnerPool;
            newGameState.lavaPool = gameState.lavaPool;
            newGameState.conveyorPool = gameState.conveyorPool;
            return newGameState;
        }

        private GameState nextFrame(GameState gameState, long frameTime, double joyTilt, double joyWobble) {
            GameState newGameState = copyOf(gameState, frameTime);
            newGameState.frameTime = frameTime;

            if (joyTilt > JoystickActuator.DEADZONE) {
                newGameState.lastInputTime = frameTime;
                if (newGameState.stage == Stage.SCREENSAVER) {
                    newGameState.levelNumber = -1;
                    newGameState.stageStartTime = frameTime;
                    newGameState.stage = Stage.LEVEL_COMPLETE;
                }
                if (newGameState.stage == Stage.GAME_OVER) {
                    newGameState.levelNumber = -1;
                    newGameState.stageStartTime = frameTime;
                    newGameState.stage = Stage.SCREENSAVER;
                }
            } else {
                if (newGameState.lastInputTime + SCREENSAVER_TIMEOUT < frameTime) {
                    newGameState.stageStartTime = frameTime;
                    newGameState.stage = Stage.SCREENSAVER;
                }
            }

            if (newGameState.stage == Stage.PLAY) {

                if (newGameState.attacking) {
                    newGameState.attacking = newGameState.attackStartedTime + ATTACK_DURATION >= frameTime;
                } else {
                    if (joyWobble > ATTACK_THRESHOLD) {
                        newGameState.attacking = true;
                        newGameState.attackStartedTime = frameTime;
                    }
                }

                int playerPosition = newGameState.playerPosition;
                playerPosition += newGameState.playerPositionModifier;
                if (!newGameState.attacking) {
                    int moveAmount = (int) (joyTilt / 6.0);
                    if (DIRECTION == LEFT_TO_RIGHT) {
                        moveAmount = -moveAmount;
                    }
                    moveAmount = constrain(moveAmount, -MAX_PLAYER_SPEED, MAX_PLAYER_SPEED);
                    playerPosition -= moveAmount;
                    playerPosition = constrain(playerPosition, 0, 1000);
                }
                newGameState.playerPosition = playerPosition;

                if (inLava(newGameState.lavaPool, newGameState.playerPosition)) { // TODO first class collection might be nice for the lava pool
                    die(newGameState, newGameState.lives, frameTime);
                }

                if (newGameState.playerPosition == 1000 && !newGameState.boss.isAlive()) {
                    // Reached exit!
                    if (newGameState.levelNumber == LEVEL_COUNT) {
                        newGameState.stageStartTime = frameTime;
                        newGameState.stage = Stage.GAME_COMPLETE;
                    } else {
                        newGameState.stageStartTime = frameTime;
                        newGameState.stage = Stage.LEVEL_COMPLETE;
                    }
                    newGameState.lives = TOTAL_LIVES;
                }

                // Ticks and draw calls
                tickConveyors(newGameState, newGameState.playerPosition);
                tickEnemySpawners(newGameState, frameTime);
                tickBoss(newGameState, frameTime);
                tickLava(newGameState, frameTime);
                tickEnemies(newGameState, frameTime);
            } else if (newGameState.stage == Stage.DEAD) {
                tickParticles(newGameState);
            }

            return newGameState;
        }

        private static int constrain(int value, int lower, int upper) {
            return Math.max(Math.min(value, upper), lower);
        }

        /**
         * Returns if the player is in active lava
         */
        private static boolean inLava(List<Lava> lavaPool, int pos) {
            for (Lava lava : lavaPool) {
                if (lava.consumes(pos)) {
                    return true;
                }
            }
            return false;
        }

        private static void die(GameState newGameState, int currentLives, long frameTime) {
            int lives = currentLives - 1;
            if (lives == 0) {
                newGameState.levelNumber = START_LEVEL;
                newGameState.lives = TOTAL_LIVES;
                newGameState.stageStartTime = frameTime;
                newGameState.stage = Stage.GAME_OVER;
            } else {
                newGameState.lives = lives;
                newGameState.stageStartTime = frameTime;
                newGameState.stage = Stage.DEAD;
            }
            for (Particle particle : newGameState.particlePool) {
                particle.spawn(newGameState.playerPosition);
            }
        }

        private static void tickConveyors(GameState newGameState, int playerPosition) {
            int playerPositionModifier = 0;
            for (Conveyor conveyor : newGameState.conveyorPool) {
                playerPositionModifier = conveyor.affect(playerPosition);
            }
            newGameState.playerPositionModifier = playerPositionModifier;
        }

        private static void tickEnemySpawners(GameState newGameState, long frameTime) {
            for (EnemySpawner enemySpawner : newGameState.enemySpawnerPool) {
                if (enemySpawner.shouldSpawn(frameTime)) {
                    EnemySpawner.Spawn spawn = enemySpawner.spawn(frameTime);
                    spawnEnemy(newGameState, spawn.getPosition(), spawn.getDirection(), spawn.getSpeed(), 0);
                }
            }
        }

        private static void spawnEnemy(GameState gameState, int pos, int dir, int speed, int wobble) {
            int playerSide = pos > gameState.playerPosition ? 1 : -1;
            gameState.enemyPool.add(new Enemy(pos, dir, speed, wobble, playerSide));
        }

        private void tickBoss(GameState newGameState, long frameTime) {
            Boss boss = newGameState.boss;
            if (!boss.isAlive()) {
                return;
            }
            int playerPosition = newGameState.playerPosition;
            int bossStartPosition = boss.getPosition() - BOSS_WIDTH / 2;
            int bossEndPosition = boss.getPosition() + BOSS_WIDTH / 2;
            // CHECK COLLISION
            if (playerPosition > bossStartPosition
                    && playerPosition < bossEndPosition) {
                die(newGameState, newGameState.lives, frameTime);
                return;
            }
            // CHECK FOR ATTACK
            if (newGameState.attacking) {
                int attackStartPosition = playerPosition + (ATTACK_WIDTH / 2);
                int attackEndPosition = playerPosition - (ATTACK_WIDTH / 2);
                if ((attackStartPosition >= bossStartPosition
                        && attackStartPosition <= bossEndPosition)
                        || attackEndPosition <= bossEndPosition
                        && attackEndPosition >= bossStartPosition) {
                    boss.hit();
                    if (boss.isAlive()) {
                        moveBoss(newGameState, frameTime);
                    } else {
                        for (EnemySpawner enemySpawner : newGameState.enemySpawnerPool) {
                            enemySpawner.kill();
                        }
                    }
                }
            }
            newGameState.boss = boss;
        }

        private static void moveBoss(GameState newGameState, long frameTime) {
            int spawnSpeed = newGameState.boss.getSpeed();
            newGameState.enemySpawnerPool.clear();
            spawnEnemySpawner(newGameState, newGameState.boss.getPosition(), spawnSpeed, 3, 0, 0, frameTime);
            spawnEnemySpawner(newGameState, newGameState.boss.getPosition(), spawnSpeed, 3, 1, 0, frameTime);
        }

        private static void spawnEnemySpawner(GameState newGameState, int position, int rate, int speed, int direction, int activate, long millis) {
            newGameState.enemySpawnerPool.add(new EnemySpawner(position, rate, speed, direction, activate, millis));
        }

        private static void tickLava(GameState newGameState, long frameTime) {
            for (Lava lava : newGameState.lavaPool) {
                lava.toggleLava(frameTime);
            }
        }

        private static void tickEnemies(GameState newGameState, long frameTime) {
            for (Enemy enemy : newGameState.enemyPool) {
                if (enemy.isAlive()) {
                    enemy.tick(frameTime);
                    int playerPosition = newGameState.playerPosition;
                    if (newGameState.attacking) {
                        int attackStartPosition = playerPosition - (ATTACK_WIDTH / 2);
                        int attackEndPosition = playerPosition + (ATTACK_WIDTH / 2);
                        if (enemy.hitAttack(attackStartPosition, attackEndPosition)) {
                            enemy.kill();
//                            killMonitor.onKill(); TODO think about this
                        }
                    }
                    if (inLava(newGameState.lavaPool, enemy.getPosition())) {
                        enemy.kill();
//                        killMonitor.onKill();
                    }
                    // hit player?
                    if (enemy.hitPlayer(playerPosition)) {
                        die(newGameState, newGameState.lives, frameTime);
                        return;
                    }
                }
            }
        }

        private static void tickParticles(GameState newGameState) {
            for (Particle particle : newGameState.particlePool) {
                if (particle.isAlive()) {
                    particle.tick(USE_GRAVITY);
                }
            }
        }

    }

    class LevelReducer {
        private static final int TOTAL_DEATH_PARTICLES = 30;

        static GameState nextLevel(GameState gameState, long frameTime) {

            gameState.levelNumber = gameState.levelNumber + 1;
            if (gameState.levelNumber > 9) { // was LEVEL_COUNT
                gameState.levelNumber = 0;  // was START_LEVEL
            }

            return loadLevel(gameState, frameTime);
        }

        static GameState loadLevel(GameState gameState, long frameTime) {
            GameState newGameState = new GameState();

            newGameState.particlePool.clear();
            newGameState.enemyPool.clear();
            newGameState.enemySpawnerPool.clear();
            newGameState.lavaPool.clear();
            newGameState.conveyorPool.clear();

            spawnDeathParticles(newGameState, TOTAL_DEATH_PARTICLES);
            newGameState.playerPosition = 0;

            switch (gameState.levelNumber) {
                case 0:
                    // Left or right?
                    newGameState.playerPosition = 200;
                    spawnEnemy(newGameState, 800, 0, 0, 0);
                    break;
                case 1:
                    // Slow moving enemy
                    spawnEnemy(newGameState, 900, 0, 1, 0);
                    break;
                case 2:
                    // Spawning enemies at exit every 2 seconds
                    spawnEnemySpawner(newGameState, 1000, 3000, 2, 0, 0, frameTime);
                    break;
                case 3:
                    // Lava intro
                    spawnLava(newGameState, 400, 490, 2000, 2000);
                    spawnEnemySpawner(newGameState, 1000, 5500, 3, 0, 0, frameTime);
                    break;
                case 4:
                    // Sin enemy
                    spawnEnemy(newGameState, 700, 1, 7, 275);
                    spawnEnemy(newGameState, 500, 1, 5, 250);
                    break;
                case 5:
                    // Conveyor
                    spawnConveyor(newGameState, 100, 600, LEFT_TO_RIGHT, 3);
                    spawnEnemy(newGameState, 800, 0, 0, 0);
                    break;
                case 6:
                    // Conveyor of enemies
                    spawnConveyor(newGameState, 50, 1000, RIGHT_TO_LEFT, 3);
                    spawnEnemy(newGameState, 300, 0, 0, 0);
                    spawnEnemy(newGameState, 400, 0, 0, 0);
                    spawnEnemy(newGameState, 500, 0, 0, 0);
                    spawnEnemy(newGameState, 600, 0, 0, 0);
                    spawnEnemy(newGameState, 700, 0, 0, 0);
                    spawnEnemy(newGameState, 800, 0, 0, 0);
                    spawnEnemy(newGameState, 900, 0, 0, 0);
                    break;
                case 7:
                    // Lava run
                    spawnLava(newGameState, 195, 300, 2000, 2000);
                    spawnLava(newGameState, 350, 455, 2000, 2000);
                    spawnLava(newGameState, 510, 610, 2000, 2000);
                    spawnLava(newGameState, 660, 760, 2000, 2000);
                    spawnEnemySpawner(newGameState, 1000, 3800, 4, 0, 0, frameTime);
                    break;
                case 8:
                    // Sin enemy #2
                    spawnEnemy(newGameState, 700, 1, 7, 275);
                    spawnEnemy(newGameState, 500, 1, 5, 250);
                    spawnEnemySpawner(newGameState, 1000, 5500, 4, 0, 3000, frameTime);
                    spawnEnemySpawner(newGameState, 0, 5500, 5, 1, 10000, frameTime);
                    spawnConveyor(newGameState, 100, 900, RIGHT_TO_LEFT, 3);
                    break;
                case 9:
                    // Boss
                    spawnBoss(newGameState, 800, 3, frameTime);
                    break;
                default:
                    throw new IllegalStateException("Unknown level {" + gameState.levelNumber + "}");
            }

            newGameState.stageStartTime = frameTime;
            newGameState.stage = Stage.PLAY;
            newGameState.lastInputTime = gameState.lastInputTime;
            newGameState.levelNumber = gameState.levelNumber;
            newGameState.lives = gameState.lives;

            return newGameState;
        }

        private static void spawnDeathParticles(GameState gameState, int total) {
            for (int i = 0; i < total; i++) {
                Particle particle = new Particle();
                gameState.particlePool.add(particle);
            }
        }

        private static void spawnEnemy(GameState gameState, int pos, int dir, int speed, int wobble) {
            int playerSide = pos > gameState.playerPosition ? 1 : -1;
            gameState.enemyPool.add(new Enemy(pos, dir, speed, wobble, playerSide));
        }

        private static void spawnLava(GameState gameState, int left, int right, int ontime, int offtime) {
            gameState.lavaPool.add(new Lava(left, right, ontime, offtime, Lava.State.OFF));
        }

        private static void spawnEnemySpawner(GameState gameState, int position, int rate, int speed, int direction, int activate, long millis) {
            gameState.enemySpawnerPool.add(new EnemySpawner(position, rate, speed, direction, activate, millis));
        }

        private static void spawnConveyor(GameState gameState, int startPoint, int endPoint, Direction dir, int speed) {
            gameState.conveyorPool.add(new Conveyor(startPoint, endPoint, dir, speed));
        }

        private static void spawnBoss(GameState gameState, int position, int lives, long frameTime) {
            Boss boss = gameState.boss;
            boss.spawn(position, lives);
            spawnEnemySpawner(gameState, boss.getPosition(), boss.getSpeed(), 3, 0, 0, frameTime);
            spawnEnemySpawner(gameState, boss.getPosition(), boss.getSpeed(), 3, 1, 0, frameTime);
        }

    }
}
