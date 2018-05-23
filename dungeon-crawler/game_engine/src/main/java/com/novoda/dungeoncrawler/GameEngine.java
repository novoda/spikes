package com.novoda.dungeoncrawler;

import java.util.ArrayList;
import java.util.List;

import static com.novoda.dungeoncrawler.Direction.LEFT_TO_RIGHT;
import static com.novoda.dungeoncrawler.Direction.RIGHT_TO_LEFT;

class GameEngine {

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

    // GAME
    private static final int MIN_REDRAW_INTERVAL = 33;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps
    private static final int TIMEOUT = 30000;
    private static final int LEVEL_COUNT = 9;
    private static final boolean USE_GRAVITY = true;     // 0/1 use gravity (LED strip going up wall)
    private static final Direction DIRECTION = Direction.LEFT_TO_RIGHT;
    private static final int START_LEVEL = 0;

    // WOBBLE ATTACK
    private static final int ATTACK_DURATION = 500;    // Duration of a wobble attack (ms)
    private static final int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    private static final int BOSS_WIDTH = 40;
    private static final int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack // TODO DOESN'T BELONG HERE

    // PLAYER
    private static final int MAX_PLAYER_SPEED = 15;     // Max move speed of the player
    private static final int CONVEYOR_SPEED = 3;

    // POOLS
    private static final Particle[] particlePool = {
            new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle()
    };
    private static final List<Enemy> enemyPool = new ArrayList<>();
    private static final List<EnemySpawner> spawnPool = new ArrayList<>();
    private static final List<Lava> lavaPool = new ArrayList<>();
    private static final List<Conveyor> conveyorPool = new ArrayList<>();
    private static final Boss boss = new Boss();

    private final StartClock clock;

    private int levelNumber = START_LEVEL;
    private long previousFrameTime = 0;           // Time of the last redraw
    private long lastInputTime = 0;

    private long attackMillis = 0;             // Time the attack started
    private boolean attacking = false;                // Is the attack in progress?
    private JoystickActuator.JoyState joyState;

    private int playerPositionModifier;        // +/- adjustment to player position
    private int playerPosition;                // Stores the player position
    private Stage stage;
    private long stageStartTime;               // Stores the time the stage changed for stages that are time based
    private int lives = 3;

    GameEngine(AttackMonitor attackMonitor, KillMonitor killMonitor, MovementMonitor movementMonitor, DeathMonitor deathMonitor, WinMonitor winMonitor, NoInputMonitor noInputMonitor, CompleteMonitor completeMonitor, GameOverMonitor gameOverMonitor, DrawCallback drawCallback, JoystickActuator inputActuator, StartClock startClock) {
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
        this.clock = startClock;
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

    public void loadLevel() {
        clock.start();
        cleanupLevel();
        playerPosition = 0;
        switch (levelNumber) {
            case 0:
                // Left or right?
                playerPosition = 200;
                spawnEnemy(800, 0, 0, 0);
                break;
            case 1:
                // Slow moving enemy
                spawnEnemy(900, 0, 1, 0);
                break;
            case 2:
                // Spawning enemies at exit every 2 seconds
                spawnSpawner(1000, 3000, 2, 0, 0, clock.millis());
                break;
            case 3:
                // Lava intro
                spawnLava(400, 490, 2000, 2000);
                spawnSpawner(1000, 5500, 3, 0, 0, clock.millis());
                break;
            case 4:
                // Sin enemy
                spawnEnemy(700, 1, 7, 275);
                spawnEnemy(500, 1, 5, 250);
                break;
            case 5:
                // Conveyor
                spawnConveyor(100, 600, LEFT_TO_RIGHT, CONVEYOR_SPEED);
                spawnEnemy(800, 0, 0, 0);
                break;
            case 6:
                // Conveyor of enemies
                spawnConveyor(50, 1000, RIGHT_TO_LEFT, CONVEYOR_SPEED);
                spawnEnemy(300, 0, 0, 0);
                spawnEnemy(400, 0, 0, 0);
                spawnEnemy(500, 0, 0, 0);
                spawnEnemy(600, 0, 0, 0);
                spawnEnemy(700, 0, 0, 0);
                spawnEnemy(800, 0, 0, 0);
                spawnEnemy(900, 0, 0, 0);
                break;
            case 7:
                // Lava run
                spawnLava(195, 300, 2000, 2000);
                spawnLava(350, 455, 2000, 2000);
                spawnLava(510, 610, 2000, 2000);
                spawnLava(660, 760, 2000, 2000);
                spawnSpawner(1000, 3800, 4, 0, 0, clock.millis());
                break;
            case 8:
                // Sin enemy #2
                spawnEnemy(700, 1, 7, 275);
                spawnEnemy(500, 1, 5, 250);
                spawnSpawner(1000, 5500, 4, 0, 3000, clock.millis());
                spawnSpawner(0, 5500, 5, 1, 10000, clock.millis());
                spawnConveyor(100, 900, RIGHT_TO_LEFT, CONVEYOR_SPEED);
                break;
            case 9:
                // Boss
                spawnBoss(800, 3);
                break;
            default:
                throw new IllegalStateException("Unknown level {" + levelNumber + "}");
        }
        stageStartTime = clock.millis();
        stage = Stage.PLAY;
        drawCallback.drawLives(lives);
    }

    private void cleanupLevel() {
        for (Particle particle : particlePool) {
            particle.kill();
        }
        enemyPool.clear();
        spawnPool.clear();
        lavaPool.clear();
        conveyorPool.clear();
        boss.kill();
    }

    private void spawnEnemy(int pos, int dir, int speed, int wobble) {
        int playerSide = pos > playerPosition ? 1 : -1;
        enemyPool.add(new Enemy(pos, dir, speed, wobble, playerSide));
    }

    private void spawnLava(int left, int right, int ontime, int offtime) {
        lavaPool.add(new Lava(left, right, ontime, offtime, Lava.State.OFF));
    }

    private void spawnConveyor(int startPoint, int endPoint, Direction dir, int speed) {
        conveyorPool.add(new Conveyor(startPoint, endPoint, dir, speed));
    }

    private void spawnSpawner(int position, int rate, int speed, int direction, int activate, long millis) {
        spawnPool.add(new EnemySpawner(position, rate, speed, direction, activate, millis));
    }

    private void spawnBoss(int position, int lives) {
        boss.spawn(position, lives);
        moveBoss();
    }

    private void moveBoss() {
        int spawnSpeed = boss.getSpeed();
        spawnPool.clear();
        spawnSpawner(boss.getPosition(), spawnSpeed, 3, 0, 0, clock.millis());
        spawnSpawner(boss.getPosition(), spawnSpeed, 3, 1, 0, clock.millis());
    }

    void loop() {
//        Log.d("TUT", "loop");
        long frameTime = clock.millis();

        if (stage == Stage.PLAY) {
            if (attacking) {
                attackMonitor.onAttack();
            } else {
                movementMonitor.onMove(joyState == null ? 0 : joyState.tilt);
            }
        } else if (stage == Stage.DEAD) {
            deathMonitor.onDeath();
        }

        joyState = inputActuator.getInput();
        if (frameTime - previousFrameTime >= MIN_REDRAW_INTERVAL) {
            previousFrameTime = frameTime;

            if (Math.abs(joyState.tilt) > JoystickActuator.DEADZONE) {
                lastInputTime = frameTime;
                if (stage == Stage.SCREENSAVER) {
                    levelNumber = START_LEVEL - 1;
                    stageStartTime = frameTime;
                    stage = Stage.LEVEL_COMPLETE;
                }
            } else {
                if (lastInputTime + TIMEOUT < frameTime) {
                    stage = Stage.SCREENSAVER;
                }
            }
            if (stage == Stage.SCREENSAVER) {
                noInputMonitor.onNoInput(frameTime);
            } else if (stage == Stage.PLAY) {
                // PLAYING
                if (attacking && attackMillis + ATTACK_DURATION < frameTime) {
                    attacking = false;
                }

                // If not attacking, check if they should be
                if (!attacking && joyState.wobble > ATTACK_THRESHOLD) {
                    attackMillis = frameTime;
                    attacking = true;
                }

                // If still not attacking, move!
                playerPosition += playerPositionModifier;
                if (!attacking) {
                    int moveAmount = (int) (joyState.tilt / 6.0);
                    if (DIRECTION == LEFT_TO_RIGHT) {
                        moveAmount = -moveAmount;
                    }
                    moveAmount = constrain(moveAmount, -MAX_PLAYER_SPEED, MAX_PLAYER_SPEED);
                    playerPosition -= moveAmount;
                    if (playerPosition < 0) {
                        playerPosition = 0;
                    }
                    if (playerPosition >= 1000 && !boss.isAlive()) {
                        // Reached exit!
                        levelComplete();
                        return;
                    }
                }

                if (inLava(playerPosition)) {
                    die();
                }

                // Ticks and draw calls
                tickConveyors();
                tickEnemySpawners();
                tickBoss();
                tickLava();
                tickEnemies();
                playingStateDraw();

            } else if (stage == Stage.DEAD) {
                // DEAD
                drawCallback.startDraw();

                tickParticles();
                drawParticles();

                if (areAllParticlesDeactive()) {
                    loadLevel();
                }
            } else if (stage == Stage.LEVEL_COMPLETE) {
                if (frameTime < stageStartTime + 1200) {
                    winMonitor.onWin(stageStartTime, frameTime);
                } else {
                    nextLevel();
                }
            } else if (stage == Stage.GAME_COMPLETE) {
                if (frameTime < stageStartTime + 5500) {
                    completeMonitor.onGameComplete(stageStartTime, frameTime);
                } else {
                    nextLevel();
                }
            } else if (stage == Stage.GAME_OVER) {
                stageStartTime = 0;
                gameOverMonitor.onGameOver();
            }

            drawCallback.finishDraw();
        }
    }

    private void playingStateDraw() {
        drawCallback.startDraw();
        long frame = 10000 + clock.millis();
        for (Conveyor conveyor : conveyorPool) {
            drawCallback.drawConveyor(conveyor.getStartPoint(), conveyor.getEndPoint(), conveyor.getDirection(), frame);
        }
        for (Enemy enemy : enemyPool) { // TODO this is now after the check if the enemy has hit the player - just sanity test this doesn't cause weird state
            if (enemy.isAlive()) {
                drawCallback.drawEnemy(enemy.position);
            }
        }
        for (Lava lava : lavaPool) {
            drawCallback.drawLava(lava.getLeft(), lava.getRight(), lava.isEnabled());
        }
        drawCallback.drawPlayer(playerPosition);
        if (attacking) {
            int startPoint = playerPosition + (ATTACK_WIDTH / 2);
            int endPoint = playerPosition - (ATTACK_WIDTH / 2);
            int attackPower = (int) GameEngine.map((int) (clock.millis() - attackMillis), 0, ATTACK_DURATION, 100, 5);
            drawCallback.drawAttack(startPoint, playerPosition, endPoint, attackPower);
        }
        if (!boss.isAlive()) {
            drawCallback.drawExit();
        }
    }

    private int constrain(int value, int lower, int upper) {
        return Math.max(Math.min(value, upper), lower);
    }

    private void levelComplete() {
        stageStartTime = clock.millis();
        stage = Stage.LEVEL_COMPLETE;
        if (levelNumber == LEVEL_COUNT) {
            stage = Stage.GAME_COMPLETE;
        }
        lives = 3;
        drawCallback.drawLives(lives);
    }

    /**
     * Returns if the player is in active lava
     */
    private boolean inLava(int pos) {
        for (Lava lava : lavaPool) {
            if (lava.consumes(pos)) {
                return true;
            }
        }
        return false;
    }

    private void die() {
        if (levelNumber > 0) {
            lives--;
        }
        drawCallback.drawLives(lives);
        if (lives == 0) {
            levelNumber = START_LEVEL;
            lives = 3;
        }
        for (Particle particle : particlePool) {
            particle.spawn(playerPosition);
        }
        stageStartTime = clock.millis();
        stage = Stage.DEAD;
    }

    private void tickConveyors() {
        playerPositionModifier = 0;
        for (Conveyor conveyor : conveyorPool) {
            playerPositionModifier = conveyor.affect(playerPosition);
        }
    }

    private void tickEnemySpawners() {
        long mm = clock.millis();
        for (EnemySpawner enemySpawner : spawnPool) {
            if (enemySpawner.shouldSpawn(mm)) {
                EnemySpawner.Spawn spawn = enemySpawner.spawn(mm);
                spawnEnemy(spawn.getPosition(), spawn.getDirection(), spawn.getSpeed(), 0);
            }
        }
    }

    private void tickBoss() {
        // DRAW
        if (!boss.isAlive()) {
            return;
        }
        int bossStartPosition = boss.getPosition() - BOSS_WIDTH / 2;
        int bossEndPosition = boss.getPosition() + BOSS_WIDTH / 2;
        drawCallback.drawBoss(bossStartPosition, bossEndPosition);
        // CHECK COLLISION
        if (playerPosition > bossStartPosition
                && playerPosition < bossEndPosition) {
            die();
            return;
        }
        // CHECK FOR ATTACK
        if (attacking) {
            int attackStartPosition = playerPosition + (ATTACK_WIDTH / 2);
            int attackEndPosition = playerPosition - (ATTACK_WIDTH / 2);
            if ((attackStartPosition >= bossStartPosition
                    && attackStartPosition <= bossEndPosition)
                    || attackEndPosition <= bossEndPosition
                    && attackEndPosition >= bossStartPosition) {
                boss.hit();
                if (boss.isAlive()) {
                    moveBoss();
                } else {
                    for (EnemySpawner enemySpawner : spawnPool) {
                        enemySpawner.kill();
                    }
                }
            }
        }
    }

    private void tickLava() {
        long mm = clock.millis();
        for (Lava lava : lavaPool) {
            lava.toggleLava(mm);
        }
    }

    private void tickEnemies() {
        for (Enemy enemy : enemyPool) {
            if (enemy.isAlive()) {
                enemy.tick(clock.millis());
                // hit attack?
                if (attacking) {
                    int attackStartPosition = playerPosition - (ATTACK_WIDTH / 2);
                    int attackEndPosition = playerPosition + (ATTACK_WIDTH / 2);
                    if (enemy.hitAttack(attackStartPosition, attackEndPosition)) {
                        enemy.kill();
                        killMonitor.onKill();
                    }
                }
                if (inLava(enemy.position)) {
                    enemy.kill();
                    killMonitor.onKill();
                }
                // hit player?
                if (enemy.hitPlayer(playerPosition)) {
                    die();
                    return;
                }
            }
        }
    }

    private void tickParticles() {
        for (Particle particle : particlePool) {
            if (particle.isAlive()) {
                particle.tick(USE_GRAVITY);
            }
        }
    }

    private void drawParticles() {
        for (Particle particle : particlePool) {
            if (particle.isAlive()) {
                drawCallback.drawParticle(particle.getPosition(), particle.getPower());
            }
        }
    }

    private boolean areAllParticlesDeactive() {
        for (Particle particle : particlePool) {
            if (particle.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private void nextLevel() {
        levelNumber++;
        if (levelNumber > LEVEL_COUNT) {
            levelNumber = START_LEVEL;
        }
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
