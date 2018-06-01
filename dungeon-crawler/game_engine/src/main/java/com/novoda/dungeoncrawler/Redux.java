package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Reducer;

import java.util.ArrayList;
import java.util.List;

import static com.novoda.dungeoncrawler.Direction.LEFT_TO_RIGHT;
import static com.novoda.dungeoncrawler.Direction.RIGHT_TO_LEFT;

public interface Redux {

    class GameState {

        StartClock clock = new StartClock();

        final List<Particle> particlePool = new ArrayList<>();
        final List<Enemy> enemyPool = new ArrayList<>();
        final List<EnemySpawner> enemySpawnerPool = new ArrayList<>();
        final List<Lava> lavaPool = new ArrayList<>();
        final List<Conveyor> conveyorPool = new ArrayList<>();
        final Boss boss = new Boss();

        int levelNumber;
        long previousFrameTime;
        long lastInputTime;

        long attackMillis;
        boolean attacking;

        int playerPositionModifier;
        int playerPosition;
        Stage stage;
        long stageStartTime;
        int lives;

        static GameState getInitialState() {
            GameState gameState = new GameState();
            gameState.clock = new StartClock();
            gameState.clock.start(); // Hack
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

        public static Action nextFrame(double joyTilt) {
            return new Action(GOTO_NEXT_FRAME.toString(), new Object[]{joyTilt});
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
                    return nextFrame(gameState, tilt);
                default:
                    throw new IllegalStateException(action.type + " is unknown.");
            }
        }

        private GameState nextFrame(GameState gameState, double joyTilt) {
            GameState newGameState = new GameState();
            return newGameState;
        }
    }

    class LevelReducer {
        private static final int TOTAL_DEATH_PARTICLES = 30;

        static GameState nextLevel(GameState gameState) {
            GameState newGameState = new GameState();
            newGameState.clock = gameState.clock; // hack

            newGameState.levelNumber = gameState.levelNumber + 1;
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

        private static void moveBoss(GameState gameState) {
            int spawnSpeed = gameState.boss.getSpeed();
            gameState.enemySpawnerPool.clear();
            spawnEnemySpawner(gameState, gameState.boss.getPosition(), spawnSpeed, 3, 0, 0, gameState.clock.millis());
            spawnEnemySpawner(gameState, gameState.boss.getPosition(), spawnSpeed, 3, 1, 0, gameState.clock.millis());
        }
    }
}
