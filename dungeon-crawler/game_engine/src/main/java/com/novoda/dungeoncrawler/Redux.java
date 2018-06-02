package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Reducer;

import java.util.ArrayList;
import java.util.List;

import static com.novoda.dungeoncrawler.Direction.LEFT_TO_RIGHT;
import static com.novoda.dungeoncrawler.Direction.RIGHT_TO_LEFT;

public interface Redux {

    class GameState {

        static StartClock clock = new StartClock();

        static final List<Particle> particlePool = new ArrayList<>();
        static final List<Enemy> enemyPool = new ArrayList<>();
        static final List<EnemySpawner> enemySpawnerPool = new ArrayList<>();
        static final List<Lava> lavaPool = new ArrayList<>();
        static final List<Conveyor> conveyorPool = new ArrayList<>();

        Boss boss = new Boss();
        int levelNumber;
        long previousFrameTime;
        long lastInputTime;

        long attackMillis; // Better name would be attackStartedTime
        boolean attacking;

        int playerPositionModifier;
        int playerPosition;
        Stage stage;
        long stageStartTime;
        int lives;

        static GameState getInitialState() {
            GameState gameState = new GameState();
            gameState.clock = new StartClock();
            gameState.clock.start();
            gameState.levelNumber = -1;
            gameState.previousFrameTime = 0;
            gameState.lastInputTime = 0;
            gameState.attackMillis = 0;
            gameState.attacking = false;
            gameState.lives = 3;
            return gameState;
        }
    }

    enum GameActions {
        GOTO_NEXT_LEVEL, RESTART_LEVEL, GOTO_NEXT_FRAME;

        public static Action nextLevel() {
            return new Action(GOTO_NEXT_LEVEL.toString());
        }

        public static Action restartLevel() {
            return new Action(RESTART_LEVEL.toString());
        }

        public static Action nextFrame(double joyTilt, double joyWobble) {
            return new Action(GOTO_NEXT_FRAME.toString(), new Object[]{joyTilt, joyWobble});
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
            switch (GameActions.valueOf(action.type)) {
                case GOTO_NEXT_LEVEL:
                    return LevelReducer.nextLevel(gameState);
                case RESTART_LEVEL:
                    return LevelReducer.loadLevel(gameState);
                case GOTO_NEXT_FRAME:
                    double tilt = action.getValue(0);
                    double wobble = action.getValue(1);
                    return nextFrame(gameState, tilt, wobble);
                default:
                    throw new IllegalStateException(action.type + " is unknown.");
            }
        }

        private static final int TIMEOUT = 30000;
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

        private GameState nextFrame(GameState gameState, double joyTilt, double joyWobble) {
            GameState newGameState = new GameState();
            newGameState.stage = gameState.stage;
//            newGameState.enemyPool.addAll(gameState.enemyPool);
//            newGameState.enemySpawnerPool.addAll(gameState.enemySpawnerPool);
//            newGameState.lavaPool.addAll(gameState.lavaPool);
//            newGameState.conveyorPool.addAll(gameState.conveyorPool);

            long frameTime = gameState.clock.millis();
            newGameState.previousFrameTime = frameTime;

            if (joyTilt > JoystickActuator.DEADZONE) {
                newGameState.lastInputTime = frameTime;
                if (gameState.stage == Stage.SCREENSAVER) {
                    newGameState.levelNumber = -1;
                    newGameState.stageStartTime = frameTime;
                    newGameState.stage = Stage.LEVEL_COMPLETE;
                }
            } else {
                if (gameState.lastInputTime + TIMEOUT < frameTime) {
                    gameState.stage = Stage.SCREENSAVER;
                }
            }

            if (gameState.stage == Stage.PLAY) {

                if (gameState.attacking) {
                    if (gameState.attackMillis + ATTACK_DURATION < frameTime) {
                        newGameState.attacking = false;
                    } else {
                        newGameState.attacking = true;
                    }
                } else {
                    if (joyWobble > ATTACK_THRESHOLD) {
                        newGameState.attacking = true;
                        newGameState.attackMillis = frameTime;
                    }
                }

                int playerPosition = gameState.playerPosition;
                playerPosition += gameState.playerPositionModifier;
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

                if (inLava(gameState.lavaPool, gameState.playerPosition)) { // TODO first class collection might be nice for the lava pool
                    die(newGameState, gameState.lives);
                }

                if (newGameState.playerPosition == 1000 && !gameState.boss.isAlive()) {
                    // Reached exit!
//                    newGameState.stageStartTime = gameState.clock.millis();  TODO not sure if needed, as also done when level is loaded
                    if (gameState.levelNumber == LEVEL_COUNT) {
                        newGameState.stage = Stage.GAME_COMPLETE;
                    } else {
                        newGameState.stage = Stage.LEVEL_COMPLETE;
                    }
                    newGameState.lives = TOTAL_LIVES;
                }

                // Ticks and draw calls
                tickConveyors(newGameState, newGameState.playerPosition);
                tickEnemySpawners(newGameState, frameTime);
                tickBoss(newGameState, gameState);
                tickLava(newGameState, frameTime);
                tickEnemies(newGameState, frameTime);
            } else if (gameState.stage == Stage.DEAD) {
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

        // GameState is an outputvariable, so gross
        private static void die(GameState newGameState, int currentLives) {
            int lives = currentLives - 1;
            if (lives == 0) {
                newGameState.levelNumber = START_LEVEL;
                newGameState.lives = TOTAL_LIVES;
            } else {
                newGameState.lives = lives;
            }
            newGameState.stage = Stage.DEAD;
            for (Particle particle : newGameState.particlePool) {
                particle.spawn(newGameState.playerPosition);
            }
        }

        // GameState is an outputvariable, so gross
        private static void tickConveyors(GameState newGameState, int playerPosition) {
            int playerPositionModifier = 0;
            for (Conveyor conveyor : newGameState.conveyorPool) {
                playerPositionModifier = conveyor.affect(playerPosition);
            }
            newGameState.playerPositionModifier = playerPositionModifier;
        }

        // GameState is an outputvariable, so gross
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

        private void tickBoss(GameState newGameState, GameState gameState) {
            Boss boss = newGameState.boss = gameState.boss;
            if (!boss.isAlive()) {
                return;
            }
            int playerPosition = newGameState.playerPosition;
            int bossStartPosition = boss.getPosition() - BOSS_WIDTH / 2;
            int bossEndPosition = boss.getPosition() + BOSS_WIDTH / 2;
            // CHECK COLLISION
            if (playerPosition > bossStartPosition
                && playerPosition < bossEndPosition) {
                die(newGameState, newGameState.lives);
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
                        moveBoss(newGameState);
                    } else {
                        for (EnemySpawner enemySpawner : newGameState.enemySpawnerPool) {
                            enemySpawner.kill();
                        }
                    }
                }
            }
            newGameState.boss = boss;
        }

        private static void moveBoss(GameState newGameState) {
            int spawnSpeed = newGameState.boss.getSpeed();
            newGameState.enemySpawnerPool.clear();
            spawnEnemySpawner(newGameState, newGameState.boss.getPosition(), spawnSpeed, 3, 0, 0, newGameState.clock.millis());
            spawnEnemySpawner(newGameState, newGameState.boss.getPosition(), spawnSpeed, 3, 1, 0, newGameState.clock.millis());
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
                        die(newGameState, newGameState.lives);
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

        static GameState nextLevel(GameState gameState) {
            GameState newGameState = new GameState();
            newGameState.clock = gameState.clock; // hack

            int levelNumber = gameState.levelNumber;
            newGameState.levelNumber = levelNumber + 1;
            if (newGameState.levelNumber > 9) { // was LEVEL_COUNT
                newGameState.levelNumber = 0;  // was START_LEVEL
            }

            return loadLevel(newGameState);
        }

        static GameState loadLevel(GameState gameState) {
            GameState newGameState = new GameState();

            spawnDeathParticles(newGameState, TOTAL_DEATH_PARTICLES);
            newGameState.playerPosition = 0;

            newGameState.clock = gameState.clock;
            StartClock clock = newGameState.clock;

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
                    spawnEnemySpawner(newGameState, 1000, 3000, 2, 0, 0, clock.millis());
                    break;
                case 3:
                    // Lava intro
                    spawnLava(newGameState, 400, 490, 2000, 2000);
                    spawnEnemySpawner(newGameState, 1000, 5500, 3, 0, 0, clock.millis());
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
                    spawnEnemySpawner(newGameState, 1000, 3800, 4, 0, 0, clock.millis());
                    break;
                case 8:
                    // Sin enemy #2
                    spawnEnemy(newGameState, 700, 1, 7, 275);
                    spawnEnemy(newGameState, 500, 1, 5, 250);
                    spawnEnemySpawner(newGameState, 1000, 5500, 4, 0, 3000, clock.millis());
                    spawnEnemySpawner(newGameState, 0, 5500, 5, 1, 10000, clock.millis());
                    spawnConveyor(newGameState, 100, 900, RIGHT_TO_LEFT, 3);
                    break;
                case 9:
                    // Boss
                    spawnBoss(newGameState, 800, 3);
                    break;
                default:
                    throw new IllegalStateException("Unknown level {" + gameState.levelNumber + "}");
            }

            newGameState.stageStartTime = clock.millis();
            newGameState.stage = Stage.PLAY;

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

        private static void spawnBoss(GameState gameState, int position, int lives) {
            gameState.boss.spawn(position, lives);
            moveBoss(gameState);
        }

        private static void moveBoss(GameState gameState) { // TODO why do we need to move the boss straight away?
            int spawnSpeed = gameState.boss.getSpeed();
            gameState.enemySpawnerPool.clear();
            spawnEnemySpawner(gameState, gameState.boss.getPosition(), spawnSpeed, 3, 0, 0, gameState.clock.millis());
            spawnEnemySpawner(gameState, gameState.boss.getPosition(), spawnSpeed, 3, 1, 0, gameState.clock.millis());
        }
    }
}