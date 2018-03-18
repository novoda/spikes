package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;

import java.util.Random;

public class MainActivity extends Activity {

    // LED setup
    private static final int NUM_LEDS = 25;//475;
    private static final int DATA_PIN = 3;
    private static final int CLOCK_PIN = 4;
    private static final int LED_COLOR_ORDER = 0;//BGR;//GBR
    private static final int BRIGHTNESS = 150;
    private static final Direction DIRECTION = Direction.LEFT_TO_RIGHT;
    private static final int MIN_REDRAW_INTERVAL = 16;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps
    private static final boolean USE_GRAVITY = true;     // 0/1 use gravity (LED strip going up wall)
    private static final int BEND_POINT = 550;   // 0/1000 point at which the LED strip goes up the wall

    // GAME
    private static final int TIMEOUT = 30000;
    private static final int LEVEL_COUNT = 9;
    private static final int MAX_VOLUME = 10;
    private long previousMillis = 0;           // Time of the last redraw
    private int levelNumber = 0;
    private long lastInputTime = 0;

    // WOBBLE ATTACK
    private static final int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack // TODO DOESN'T BELONG HERE
    private static final int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    private static final int ATTACK_DURATION = 500;    // Duration of a wobble attack (ms)
    private static final int BOSS_WIDTH = 40;
    private long attackMillis = 0;             // Time the attack started
    private boolean attacking = false;                // Is the attack in progress?

    // PLAYER
    private static final Display.CRGB PLAYER_COLOR = Display.CRGB.GREEN;
    private static final int MAX_PLAYER_SPEED = 10;     // Max move speed of the player
    private Stage stage;
    private long stageStartTime;               // Stores the time the stage changed for stages that are time based
    private int playerPosition;                // Stores the player position
    private int playerPositionModifier;        // +/- adjustment to player position
    private boolean playerAlive;
    private long killTime;
    private int lives = 3;

    // POOLS
    private static final int[] lifeLEDs = new int[]{52, 50, 40};

    private static final Enemy[] enemyPool = new Enemy[]{
        new Enemy(), new Enemy(), new Enemy(), new Enemy(), new Enemy(),
        new Enemy(), new Enemy(), new Enemy(), new Enemy(), new Enemy()
    };

    private static final Particle[] particlePool = {
        new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle()
    };
    private static final Spawner[] spawnPool = {
        new Spawner(), new Spawner()
    };
    private static final Lava[] lavaPool = {
        new Lava(), new Lava(), new Lava(), new Lava()
    };
    private static final Conveyor[] conveyorPool = {
        new Conveyor(), new Conveyor()
    };
    private static final Boss boss = new Boss();

    private ArduinoLoop arduinoLoop = new ArduinoLoop();
    private Display ledStrip;
    private Joystick joystick;
    private Joystick.JoyState joyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystick = new Joystick(new MPU6050());
        joystick.initialise();

//         Fast LED
//        ledStrip = new FastLED(NUM_LEDS, LED_COLOR_ORDER, DATA_PIN, CLOCK_PIN);
//        ledStrip = new LogcatDisplay(NUM_LEDS);
        ledStrip = new AndroidDeviceDisplay(this, findViewById(R.id.scrollView), NUM_LEDS);
//        ledStrip.setBrightness(BRIGHTNESS);
//        ledStrip.setDither(1);

        // Life LEDs
        for (int i = 0; i < 3; i++) {
//            pinMode(lifeLEDs[i], OUTPUT);
            digitalWrite(lifeLEDs[i], Gpio.ACTIVE_HIGH);
        }

        findViewById(R.id.button2).setOnClickListener(v -> {
            loadLevel();
        });

        loadLevel();

        arduinoLoop.start(this::loop);
    }

    // ---------------------------------
// ------------ LEVELS -------------
// ---------------------------------
    void loadLevel() {
        Log.d("TUT", "Game Starting");
        updateLives();
        cleanupLevel();
        playerPosition = 0;
        playerAlive = true;
        switch (levelNumber) {
            case 0:
                // Left or right?
                playerPosition = 200;
                spawnEnemy(1, 0, 0, 0);
                break;
            case 1:
                // Slow moving enemy
                spawnEnemy(900, 0, 1, 0);
                break;
            case 2:
                // Spawning enemies at exit every 2 seconds
                spawnPool[0].spawn(1000, 3000, 2, 0, 0, millis());
                break;
            case 3:
                // Lava intro
                spawnLava(400, 490, 2000, 2000, 0);
                spawnPool[0].spawn(1000, 5500, 3, 0, 0, millis());
                break;
            case 4:
                // Sin enemy
                spawnEnemy(700, 1, 7, 275);
                spawnEnemy(500, 1, 5, 250);
                break;
            case 5:
                // Conveyor
                spawnConveyor(100, 600, -1);
                spawnEnemy(800, 0, 0, 0);
                break;
            case 6:
                // Conveyor of enemies
                spawnConveyor(50, 1000, 1);
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
                spawnLava(195, 300, 2000, 2000, 0);
                spawnLava(350, 455, 2000, 2000, 0);
                spawnLava(510, 610, 2000, 2000, 0);
                spawnLava(660, 760, 2000, 2000, 0);
                spawnPool[0].spawn(1000, 3800, 4, 0, 0, millis());
                break;
            case 8:
                // Sin enemy #2
                spawnEnemy(700, 1, 7, 275);
                spawnEnemy(500, 1, 5, 250);
                spawnPool[0].spawn(1000, 5500, 4, 0, 3000, millis());
                spawnPool[1].spawn(0, 5500, 5, 1, 10000, millis());
                spawnConveyor(100, 900, -1);
                break;
            case 9:
                // Boss
                spawnBoss();
                break;
        }
        stageStartTime = millis();
        stage = Stage.PLAY;
    }

    private long millis = 0;

    /**
     * https://www.arduino.cc/reference/en/language/functions/time/millis/
     * TODO
     *
     * @return Number of milliseconds since the program started (unsigned long)
     */
    private long millis() {
        if (millis == 0) {
            millis = System.currentTimeMillis();
        }
        return System.currentTimeMillis() - millis;
    }

    void updateLives() {
        // Updates the life LEDs to show how many lives the player has left
        for (int i = 0; i < 3; i++) {
            digitalWrite(lifeLEDs[i], lives > i ? Gpio.ACTIVE_HIGH : Gpio.ACTIVE_LOW);
        }
    }

    // TODO GPIO
    private void digitalWrite(int pin, int value) {
        Log.d("TUT", "Digital write pin " + pin + " value " + value);
    }

    void cleanupLevel() {
        for (Enemy anEnemyPool : enemyPool) {
            anEnemyPool.kill();
        }
        for (Particle aParticlePool : particlePool) {
            aParticlePool.kill();
        }
        for (Spawner aSpawnPool : spawnPool) {
            aSpawnPool.kill();
        }
        for (Lava aLavaPool : lavaPool) {
            aLavaPool.kill();
        }
        for (Conveyor aConveyorPool : conveyorPool) {
            aConveyorPool.kill();
        }
        boss.kill();
    }

    void spawnEnemy(int pos, int dir, int sp, int wobble) {
        for (Enemy anEnemyPool : enemyPool) {
            if (!anEnemyPool.isAlive()) {
                anEnemyPool.spawn(pos, dir, sp, wobble);
                anEnemyPool.playerSide = pos > playerPosition ? 1 : -1;
                return;
            }
        }
    }

    void spawnLava(int left, int right, int ontime, int offtime, int offset) {
        for (Lava aLavaPool : lavaPool) {
            if (!aLavaPool.isAlive()) {
                aLavaPool.spawn(left, right, ontime, offtime, offset, Lava.State.OFF, millis());
                return;
            }
        }
    }

    void spawnConveyor(int startPoint, int endPoint, int dir) {
        for (Conveyor aConveyorPool : conveyorPool) {
            if (!aConveyorPool.isAlive()) {
                aConveyorPool.spawn(startPoint, endPoint, dir);
                return;
            }
        }
    }

    void spawnBoss() {
        boss.spawn();
        moveBoss();
    }

    void moveBoss() {
        int spawnSpeed = 2500;
        if (boss.lives == 2) {
            spawnSpeed = 2000;
        }
        if (boss.lives == 1) {
            spawnSpeed = 1500;
        }
        spawnPool[0].spawn(boss.position, spawnSpeed, 3, 0, 0, millis());
        spawnPool[1].spawn(boss.position, spawnSpeed, 3, 1, 0, millis());
    }

    private void loop() {
//        Log.d("TUT", "loop");
        long mm = millis();

        if (stage == Stage.PLAY) {
            if (attacking) {
                SFXattacking();
            } else {
                SFXtilt(joyState == null ? 0 : joyState.tilt);
            }
        } else if (stage == Stage.DEAD) {
            SFXdead();
        }

        if (mm - previousMillis >= MIN_REDRAW_INTERVAL) {
            getInput();
            long frameTimer = mm;
            previousMillis = mm;

            if (Math.abs(joyState.tilt) > Joystick.DEADZONE) {
                lastInputTime = mm;
                if (stage == Stage.SCREENSAVER) {
                    levelNumber = -1;
                    stageStartTime = mm;
                    stage = Stage.WIN;
                }
            } else {
                if (lastInputTime + TIMEOUT < mm) {
                    stage = Stage.SCREENSAVER;
                }
            }
            if (stage == Stage.SCREENSAVER) {
                ledStrip.clear(); // TODO I added this
                screenSaverTick();
            } else if (stage == Stage.PLAY) {
                // PLAYING
                if (attacking && attackMillis + ATTACK_DURATION < mm) {
                    attacking = false;
                }

                // If not attacking, check if they should be
                if (!attacking && joyState.wobble > ATTACK_THRESHOLD) {
                    attackMillis = mm;
                    attacking = true;
                }

                // If still not attacking, move!
                playerPosition += playerPositionModifier;
                if (!attacking) {
                    int moveAmount = (int) (joyState.tilt / 6.0);
                    if (DIRECTION == Direction.LEFT_TO_RIGHT) {
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
                ledStrip.clear();
                tickConveyors();
                tickSpawners();
                tickBoss();
                tickLava();
                tickEnemies();
                drawPlayer();
                drawAttack();
                drawExit();
            } else if (stage == Stage.DEAD) {
                // DEAD
                ledStrip.clear();
                if (!tickParticles()) {
                    loadLevel();
                }
            } else if (stage == Stage.WIN) {
                // LEVEL COMPLETE
                ledStrip.clear();
                if (stageStartTime + 500 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 0, 500, NUM_LEDS, 0), 0);
                    for (int i = NUM_LEDS; i >= n; i--) {
                        ledStrip.set(i, PLAYER_COLOR);
                    }
                    SFXwin();
                } else if (stageStartTime + 1000 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 500, 1000, NUM_LEDS, 0), 0);
                    for (int i = 0; i < n; i++) {
                        ledStrip.set(i, PLAYER_COLOR);
                    }
                    SFXwin();
                } else if (stageStartTime + 1200 > mm) {
                    ledStrip.set(0, PLAYER_COLOR);
                } else {
                    nextLevel();
                }
            } else if (stage == Stage.GAME_COMPLETE) {
                ledStrip.clear();
                SFXcomplete();
                if (stageStartTime + 500 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 0, 500, NUM_LEDS, 0), 0);
                    for (int i = NUM_LEDS; i >= n; i--) {
                        int brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        ledStrip.modifyHSV(i, brightness, 255, 50);
                    }
                } else if (stageStartTime + 5000 > mm) {
                    for (int i = NUM_LEDS; i >= 0; i--) {
                        int brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        ledStrip.modifyHSV(i, brightness, 255, 50);
                    }
                } else if (stageStartTime + 5500 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 5000, 5500, NUM_LEDS, 0), 0);
                    for (int i = 0; i < n; i++) {
                        int brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        ledStrip.modifyHSV(i, brightness, 255, 50);
                    }
                } else {
                    nextLevel();
                }
            } else if (stage == Stage.GAME_OVER) {
                // GAME OVER!
                ledStrip.clear();
                stageStartTime = 0;
            }

//            Log.d("TUT", "" + (millis() - mm));
//            Log.d("TUT", " - ");
            ledStrip.show();
//            Log.d("TUT", "" + (millis() - mm));
        }
    }

    // ---------------------------------
// ----------- Sound Effects ------------
// ---------------------------------

    void SFXattacking() {
//        int freq = map(sin(millis() / 2.0) * 1000.0, -1000, 1000, 500, 600);
//        if (random8(5) == 0) {
//            freq *= 3;
//        }
//        toneAC(freq, MAX_VOLUME);
        Log.d("TUT", "attacking");
    }

    void SFXtilt(int amount) {
//        int f = map(abs(amount), 0, 90, 80, 900) + random8(100);
//        if (playerPositionModifier < 0) f -= 500;
//        if (playerPositionModifier > 0) f += 200;
//        toneAC(f, min(min(abs(amount) / 9, 5), MAX_VOLUME));
        if (amount != 0) {
            Log.d("TUT", "tilt " + amount);
        }
    }

    void SFXdead() {
//        int freq = max(1000 - (millis() - killTime), 10);
//        freq += random8(200);
//        int vol = max(10 - (millis() - killTime) / 200, 0);
//        toneAC(freq, MAX_VOLUME);
        Log.d("TUT", "dead");
    }

    void SFXkill() {
//        toneAC(2000, MAX_VOLUME, 1000, true);
        Log.d("TUT", "kill");
    }

    void SFXwin() {
//        int freq = (millis() - stageStartTime) / 3.0;
//        freq += map(sin(millis() / 20.0) * 1000.0, -1000, 1000, 0, 20);
//        int vol = 10;//max(10 - (millis()-stageStartTime)/200, 0);
//        toneAC(freq, MAX_VOLUME);
        Log.d("TUT", "win");
    }

    void SFXcomplete() {
//        noToneAC();
        Log.d("TUT", "complete");
    }

    void getInput() {
        joyState = joystick.getInput();
    }

    // ---------------------------------
// --------- SCREENSAVER -----------
// ---------------------------------
    void screenSaverTick() {
        int n, b, c, i;
        long mm = millis();
        int mode = (int) ((mm / 20000) % 2);

        for (i = 0; i < NUM_LEDS; i++) {
            ledStrip.modifyScale(i, 250);
        }
        if (mode == 0) {
            // Marching green <> orange
            n = (int) ((mm / 250) % 10);
            b = (int) (10 + ((Math.sin(mm / 500.00) + 1) * 20.00));
            c = (int) (20 + ((Math.sin(mm / 5000.00) + 1) * 33));
            for (i = 0; i < NUM_LEDS; i++) {
                if (i % 10 == n) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    LEDS[i] = CHSV(c, 255, 150);
                    ledStrip.set(i, new Display.CHSV(c, 255, 150));
                }
            }
        } else if (mode == 1) {
            // Random flashes
            Random random = new Random(mm);
            for (i = 0; i < NUM_LEDS; i++) {
                if (random.nextInt(200) == 0) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    LEDS[i] = CHSV(25, 255, 100);
                    ledStrip.set(i, new Display.CHSV(25, 255, 100));
                }
            }
        }
    }

    void levelComplete() {
        stageStartTime = millis();
        stage = Stage.WIN;
        if (levelNumber == LEVEL_COUNT) {
            stage = Stage.GAME_COMPLETE;
        }
        lives = 3;
        updateLives();
    }

    boolean inLava(int pos) {
        // Returns if the player is in active lava
        int i;
        Lava lava;
        for (i = 0; i < lavaPool.length; i++) {
            lava = lavaPool[i];
            if (lava.isAlive() && lava.isEnabled()) {
                if (lava.left < pos && lava.right > pos) {
                    return true;
                }
            }
        }
        return false;
    }

    void die() {
        playerAlive = false;
        if (levelNumber > 0) {
            lives--;
        }
        updateLives();
        if (lives == 0) {
            levelNumber = 0;
            lives = 3;
        }
        for (Particle aParticlePool : particlePool) {
            aParticlePool.spawn(playerPosition);
        }
        stageStartTime = millis();
        stage = Stage.DEAD;
        killTime = millis();
    }

    void tickConveyors() {
        int blue;
        int direction;
        int n;
        int i;
        int startPosition;
        int endPosition;
        int led;
        long m = 10000 + millis();
        playerPositionModifier = 0;

        for (i = 0; i < conveyorPool.length; i++) {
            if (conveyorPool[i].isAlive()) {
                direction = conveyorPool[i].direction;
                startPosition = getLED(conveyorPool[i].startPoint);
                endPosition = getLED(conveyorPool[i].endPoint);
                for (led = startPosition; led < endPosition; led++) {
                    if (direction == -1) {
                        n = (int) ((led + (m / 100)) % 5);
                    } else {
                        n = (int) ((-led + (m / 100)) % 5);
                    }
                    blue = (int) ((5 - n) / 2.0);
                    if (blue > 0) {
                        ledStrip.set(led, new Display.CRGB(0, 0, blue));
                    }
                }

                if (playerPosition > conveyorPool[i].startPoint && playerPosition < conveyorPool[i].endPoint) {
                    if (direction == -1) {
                        playerPositionModifier = -(MAX_PLAYER_SPEED - 4);
                    } else {
                        playerPositionModifier = (MAX_PLAYER_SPEED - 4);
                    }
                }
            }
        }
    }

    int getLED(int pos) {
        // The world is 1000 pixels wide, this converts world units into an LED number
        return constrain((int) map(pos, 0, 1000, 0, NUM_LEDS - 1), 0, NUM_LEDS - 1);
    }

    public static double map(int valueCoord1,
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

    private int constrain(int value, int lower, int upper) {
        return Math.max(Math.min(value, upper), lower);
    }

    void tickSpawners() {
        long mm = millis();
        for (Spawner aSpawnPool : spawnPool) {
            if (aSpawnPool.isAlive() && aSpawnPool.activate < mm) {
                if (aSpawnPool.lastSpawned + aSpawnPool.rate < mm || aSpawnPool.lastSpawned == 0) {
                    spawnEnemy(aSpawnPool.position, aSpawnPool.direction, aSpawnPool.sp, 0);
                    aSpawnPool.lastSpawned = mm;
                }
            }
        }
    }

    void tickBoss() {
        // DRAW
        if (boss.isAlive()) {
            boss.ticks++;
            for (int i = getLED(boss.position - BOSS_WIDTH / 2); i <= getLED(boss.position + BOSS_WIDTH / 2); i++) {
                ledStrip.set(i, Display.CRGB.DARK_RED);
                ledStrip.modifyMod(i, 100);
            }
            // CHECK COLLISION
            if (getLED(playerPosition) > getLED(boss.position - BOSS_WIDTH / 2) && getLED(playerPosition) < getLED(boss.position + BOSS_WIDTH)) {
                die();
                return;
            }
            // CHECK FOR ATTACK
            if (attacking) {
                if ((getLED(playerPosition + (ATTACK_WIDTH / 2)) >= getLED(boss.position - BOSS_WIDTH / 2)
                    && getLED(playerPosition + (ATTACK_WIDTH / 2)) <= getLED(boss.position + BOSS_WIDTH / 2))
                    || (getLED(playerPosition - (ATTACK_WIDTH / 2)) <= getLED(boss.position + BOSS_WIDTH / 2)
                    && getLED(playerPosition - (ATTACK_WIDTH / 2)) >= getLED(boss.position - BOSS_WIDTH / 2))) {
                    boss.hit();
                    if (boss.isAlive()) {
                        moveBoss();
                    } else {
                        spawnPool[0].kill();
                        spawnPool[1].kill();
                    }
                }
            }
        }
    }

    void tickLava() {
        int A, B, p, i, brightness, flicker;
        long mm = millis();
        Lava lava;
        for (i = 0; i < lavaPool.length; i++) {
            flicker = new Random().nextInt(5);
            lava = lavaPool[i];
            if (lava.isAlive()) {
                A = getLED(lava.left);
                B = getLED(lava.right);
                if (lava.isEnabled()) {
                    if (lava.lastOn + lava.ontime < mm) {
                        lava.disable();
                        lava.lastOn = mm;
                    }
                    for (p = A; p <= B; p++) {
                        ledStrip.set(p, new Display.CRGB(150 + flicker, 100 + flicker, 0));
                    }
                } else {
                    if (lava.lastOn + lava.offtime < mm) {
                        lava.enable();
                        lava.lastOn = mm;
                    }
                    for (p = A; p <= B; p++) {
                        ledStrip.set(p, new Display.CRGB(3 + flicker, (int) ((3 + flicker) / 1.5), 0));
                    }
                }
            }
            lavaPool[i] = lava;
        }
    }

    void tickEnemies() {
        for (Enemy anEnemyPool : enemyPool) {
            if (anEnemyPool.isAlive()) {
                anEnemyPool.tick(millis());
                // hit attack?
                if (attacking) {
                    if (anEnemyPool.position > playerPosition - (ATTACK_WIDTH / 2) && anEnemyPool.position < playerPosition + (ATTACK_WIDTH / 2)) {
                        anEnemyPool.kill();
                        SFXkill();
                    }
                }
                if (inLava(anEnemyPool.position)) {
                    anEnemyPool.kill();
                    SFXkill();
                }
                // Draw (if still alive)
                if (anEnemyPool.isAlive()) {
                    int p = getLED(anEnemyPool.position);
                    ledStrip.set(p, Display.CRGB.RED);
                }
                // hit player?
                if (
                    (anEnemyPool.playerSide == 1 && anEnemyPool.position <= playerPosition) ||
                        (anEnemyPool.playerSide == -1 && anEnemyPool.position >= playerPosition)
                    ) {
                    die();
                    return;
                }
            }
        }
    }

    private boolean tickParticles() {
        boolean stillActive = false;
        for (Particle aParticlePool : particlePool) {
            if (aParticlePool.isAlive()) {
                aParticlePool.tick(USE_GRAVITY);
//  TODO:       LEDS[getLED(particlePool[p].position)] += CRGB.(particlePool[p].power, 0, 0);
                stillActive = true;
            }
        }
        return stillActive;
    }

    private void drawPlayer() {
        int p = getLED(playerPosition);
        ledStrip.set(p, PLAYER_COLOR);
    }

    void drawAttack() {
        if (!attacking) {
            return;
        }
        // TODO check casts
        int n = (int) map((int) (millis() - attackMillis), 0, ATTACK_DURATION, 100, 5);
        int forwardPosition = playerPosition + (ATTACK_WIDTH / 2);
        int rearPosition = playerPosition - (ATTACK_WIDTH / 2);
        for (int i = getLED(rearPosition) + 1; i <= getLED(forwardPosition) - 1; i++) {
            ledStrip.set(i, new Display.CRGB(0, 0, n));
        }
        int i = getLED(playerPosition);
        if (n > 90) {
            n = 255;
            ledStrip.set(i, Display.CRGB.WHITE);
        } else {
            n = 0;
            ledStrip.set(i, Display.CRGB.GREEN);
        }
        ledStrip.set(getLED(rearPosition), new Display.CRGB(n, n, 255));
        ledStrip.set(getLED(forwardPosition), new Display.CRGB(n, n, 255));
    }

    private void drawExit() {
        if (boss.isAlive()) {
            return;
        }
        int exitPosition = NUM_LEDS - 1;
        ledStrip.set(exitPosition, new Display.CRGB(0, 0, 255));
    }

    void nextLevel() {
        levelNumber++;
        if (levelNumber > LEVEL_COUNT) {
            levelNumber = 0;
        }
        loadLevel();
    }

    @Override
    protected void onDestroy() {
        arduinoLoop.stop();
        super.onDestroy();
    }
}
