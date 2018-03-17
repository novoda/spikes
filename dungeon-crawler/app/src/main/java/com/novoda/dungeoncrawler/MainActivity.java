package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;

import java.util.Random;

public class MainActivity extends Activity {

    // MPU
    private MPU6050 accelGyro;

    // LED setup
    private static final int NUM_LEDS = 475;
    private static final int DATA_PIN = 3;
    private static final int CLOCK_PIN = 4;
    private static final int LED_COLOR_ORDER = 0;//BGR;//GBR
    private static final int BRIGHTNESS = 150;
    private static final int DIRECTION = 1;     // 0 = right to left, 1 = left to right
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

    // JOYSTICK
    private static final int JOYSTICK_ORIENTATION = 1;     // 0, 1 or 2 to set the angle of the joystick
    private static final int JOYSTICK_DIRECTION = 1;     // 0/1 to flip joystick direction
    private static final int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack
    private static final int JOYSTICK_DEADZONE = 5;     // Angle to ignore
    private int joystickTilt = 0;              // Stores the angle of the joystick
    private int joystickWobble = 0;            // Stores the max amount of acceleration (wobble)

    // WOBBLE ATTACK
    private static final int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    private static final int ATTACK_DURATION = 500;    // Duration of a wobble attack (ms)
    private static final int BOSS_WIDTH = 40;
    private long attackMillis = 0;             // Time the attack started
    private boolean attacking = false;                // Is the attack in progress?

    // PLAYER
    private static final int MAX_PLAYER_SPEED = 10;     // Max move speed of the player
    private String stage;                       // what stage the game is at (PLAY/DEAD/WIN/GAMEOVER)
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

    private static final int ENEMY_COUNT = 10;
    private static final Particle[] particlePool = {
        new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle()
    };
    private static final int PARTICLE_COUNT = 40;
    private static final Spawner[] spawnPool = {
        new Spawner(), new Spawner()
    };
    private static final int SPAWN_COUNT = 2;
    private static final Lava[] lavaPool = {
        new Lava(), new Lava(), new Lava(), new Lava()
    };
    private static final int LAVA_COUNT = 4;
    private static final Conveyor[] conveyorPool = {
        new Conveyor(), new Conveyor()
    };
    private static final int CONVEYOR_COUNT = 2;
    private static final Boss boss = new Boss();

    private static final CRGB[] LEDS = new CRGB[NUM_LEDS];
    private static final RunningMedian MPU_ANGLE_SAMPLES = new RunningMedian(5);
    private static final RunningMedian MPU_WOBBLE_SAMPLES = new RunningMedian(5);

    private ArduinoLoop arduinoLoop = new ArduinoLoop();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelGyro.initialize();

//         Fast LED
//        FastLED.addLeds<APA102, DATA_PIN, CLOCK_PIN, LED_COLOR_ORDER> (LEDS, NUM_LEDS);
//        FastLED.setBrightness(BRIGHTNESS);
//        FastLED.setDither(1);

        // Life LEDs
//        for (int i = 0; i < 3; i++) {
//            pinMode(lifeLEDs[i], OUTPUT);
//            digitalWrite(lifeLEDs[i], Gpio.ACTIVE_HIGH);
//        }

        loadLevel();

        arduinoLoop.start(this::loop);
    }

    // ---------------------------------
// ------------ LEVELS -------------
// ---------------------------------
    void loadLevel() {
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
                spawnLava(400, 490, 2000, 2000, 0, "OFF");
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
                spawnLava(195, 300, 2000, 2000, 0, "OFF");
                spawnLava(350, 455, 2000, 2000, 0, "OFF");
                spawnLava(510, 610, 2000, 2000, 0, "OFF");
                spawnLava(660, 760, 2000, 2000, 0, "OFF");
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
        stage = "PLAY";
    }

    /**
     * https://www.arduino.cc/reference/en/language/functions/time/millis/
     * TODO
     *
     * @return Number of milliseconds since the program started (unsigned long)
     */
    private long millis() {
        return 0;
    }

    void updateLives() {
        // Updates the life LEDs to show how many lives the player has left
        for (int i = 0; i < 3; i++) {
            digitalWrite(lifeLEDs[i], lives > i ? Gpio.ACTIVE_HIGH : Gpio.ACTIVE_LOW);
        }
    }

    // TODO GPIO
    private void digitalWrite(int pin, int value) {

    }

    void cleanupLevel() {
        for (int i = 0; i < ENEMY_COUNT; i++) {
            enemyPool[i].kill();
        }
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particlePool[i].kill();
        }
        for (int i = 0; i < SPAWN_COUNT; i++) {
            spawnPool[i].kill();
        }
        for (int i = 0; i < LAVA_COUNT; i++) {
            lavaPool[i].kill();
        }
        for (int i = 0; i < CONVEYOR_COUNT; i++) {
            conveyorPool[i].kill();
        }
        boss.kill();
    }

    void spawnEnemy(int pos, int dir, int sp, int wobble) {
        for (int e = 0; e < ENEMY_COUNT; e++) {
            if (!enemyPool[e].isAlive()) {
                enemyPool[e].spawn(pos, dir, sp, wobble);
                enemyPool[e].playerSide = pos > playerPosition ? 1 : -1;
                return;
            }
        }
    }

    void spawnLava(int left, int right, int ontime, int offtime, int offset, String state) {
        for (int i = 0; i < LAVA_COUNT; i++) {
            if (!lavaPool[i].isAlive()) {
                lavaPool[i].Spawn(left, right, ontime, offtime, offset, state, millis());
                return;
            }
        }
    }

    void spawnConveyor(int startPoint, int endPoint, int dir) {
        for (int i = 0; i < CONVEYOR_COUNT; i++) {
            if (!conveyorPool[i].isAlive()) {
                conveyorPool[i].spawn(startPoint, endPoint, dir);
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
        long mm = millis();
        int brightness = 0;

        if (stage.equals("PLAY")) {
            if (attacking) {
                SFXattacking();
            } else {
                SFXtilt(joystickTilt);
            }
        } else if (stage.equals("DEAD")) {
            SFXdead();
        }

        if (mm - previousMillis >= MIN_REDRAW_INTERVAL) {
            getInput();
            long frameTimer = mm;
            previousMillis = mm;

            if (Math.abs(joystickTilt) > JOYSTICK_DEADZONE) {
                lastInputTime = mm;
                if (stage.equals("SCREENSAVER")) {
                    levelNumber = -1;
                    stageStartTime = mm;
                    stage = "WIN";
                }
            } else {
                if (lastInputTime + TIMEOUT < mm) {
                    stage = "SCREENSAVER";
                }
            }
            if (stage.equals("SCREENSAVER")) {
                screenSaverTick();
            } else if (stage.equals("PLAY")) {
                // PLAYING
                if (attacking && attackMillis + ATTACK_DURATION < mm) {
                    attacking = false;
                }

                // If not attacking, check if they should be
                if (!attacking && joystickWobble > ATTACK_THRESHOLD) {
                    attackMillis = mm;
                    attacking = true;
                }

                // If still not attacking, move!
                playerPosition += playerPositionModifier;
                if (!attacking) {
                    int moveAmount = (int) (joystickTilt / 6.0);
                    if (DIRECTION == 1) { // TODO check, could be == 0
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
//                FastLED.clear(); TODO
                tickConveyors();
                tickSpawners();
                tickBoss();
                tickLava();
                tickEnemies();
                drawPlayer();
                drawAttack();
                drawExit();
            } else if (stage.equals("DEAD")) {
                // DEAD
//                FastLED.clear(); TODO
                if (!tickParticles()) {
                    loadLevel();
                }
            } else if (stage.equals("WIN")) {
                // LEVEL COMPLETE
//                FastLED.clear(); TODO
                if (stageStartTime + 500 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 0, 500, NUM_LEDS, 0), 0);
                    for (int i = NUM_LEDS; i >= n; i--) {
                        brightness = 255;
                        LEDS[i] = CRGB.create(0, brightness, 0); // TODO CRGB(0, brightness, 0)
                    }
                    SFXwin();
                } else if (stageStartTime + 1000 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 500, 1000, NUM_LEDS, 0), 0);
                    for (int i = 0; i < n; i++) {
                        brightness = 255;
                        LEDS[i] = CRGB.create(0, brightness, 0); // TODO
                    }
                    SFXwin();
                } else if (stageStartTime + 1200 > mm) {
                    LEDS[0] = CRGB.create(0, 255, 0); // TODO
                } else {
                    nextLevel();
                }
            } else if (stage.equals("COMPLETE")) {
//                FastLED.clear();  TODO
                SFXcomplete();
                if (stageStartTime + 500 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 0, 500, NUM_LEDS, 0), 0);
                    for (int i = NUM_LEDS; i >= n; i--) {
                        brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        LEDS[i].setHSV(brightness, 255, 50);
                    }
                } else if (stageStartTime + 5000 > mm) {
                    for (int i = NUM_LEDS; i >= 0; i--) {
                        brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        LEDS[i].setHSV(brightness, 255, 50);
                    }
                } else if (stageStartTime + 5500 > mm) {
                    int n = (int) Math.max(map((int) (mm - stageStartTime), 5000, 5500, NUM_LEDS, 0), 0);
                    for (int i = 0; i < n; i++) {
                        brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        LEDS[i].setHSV(brightness, 255, 50);
                    }
                } else {
                    nextLevel();
                }
            } else if (stage.equals("GAMEOVER")) {
                // GAME OVER!
//                FastLED.clear();  TODO
                stageStartTime = 0;
            }

            Log.d("TUT", "" + (millis() - mm));
            Log.d("TUT", " - ");
//            FastLED.show(); TODO
            Log.d("TUT", "" + (millis() - mm));
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
    }

    void SFXtilt(int amount) {
//        int f = map(abs(amount), 0, 90, 80, 900) + random8(100);
//        if (playerPositionModifier < 0) f -= 500;
//        if (playerPositionModifier > 0) f += 200;
//        toneAC(f, min(min(abs(amount) / 9, 5), MAX_VOLUME));
    }

    void SFXdead() {
//        int freq = max(1000 - (millis() - killTime), 10);
//        freq += random8(200);
//        int vol = max(10 - (millis() - killTime) / 200, 0);
//        toneAC(freq, MAX_VOLUME);
    }

    void SFXkill() {
//        toneAC(2000, MAX_VOLUME, 1000, true);
    }

    void SFXwin() {
//        int freq = (millis() - stageStartTime) / 3.0;
//        freq += map(sin(millis() / 20.0) * 1000.0, -1000, 1000, 0, 20);
//        int vol = 10;//max(10 - (millis()-stageStartTime)/200, 0);
//        toneAC(freq, MAX_VOLUME);
    }

    void SFXcomplete() {
//        noToneAC();
    }

    // ---------------------------------
// ----------- JOYSTICK ------------
// ---------------------------------
    void getInput() {
        // This is responsible for the player movement speed and attacking.
        // You can replace it with anything you want that passes a -90>+90 value to joystickTilt
        // and any value to joystickWobble that is greater than ATTACK_THRESHOLD (defined at start)
        // For example you could use 3 momentery buttons:
        // if(digitalRead(leftButtonPinNumber) == HIGH) joystickTilt = -90;
        // if(digitalRead(rightButtonPinNumber) == HIGH) joystickTilt = 90;
        // if(digitalRead(attackButtonPinNumber) == HIGH) joystickWobble = ATTACK_THRESHOLD;

        MPU6050.Motion motion = accelGyro.getMotion6();
        int a = (JOYSTICK_ORIENTATION == 0 ? motion.ax : (JOYSTICK_ORIENTATION == 1 ? motion.ay : motion.az)) / 166;
        int g = (JOYSTICK_ORIENTATION == 0 ? motion.gx : (JOYSTICK_ORIENTATION == 1 ? motion.gy : motion.gz));
        if (Math.abs(a) < JOYSTICK_DEADZONE) {
            a = 0;
        }
        if (a > 0) {
            a -= JOYSTICK_DEADZONE;
        }
        if (a < 0) {
            a += JOYSTICK_DEADZONE;
        }
        MPU_ANGLE_SAMPLES.add(a);
        MPU_WOBBLE_SAMPLES.add(g);

        joystickTilt = MPU_ANGLE_SAMPLES.getMedian();
        if (JOYSTICK_DIRECTION == 1) {
            joystickTilt = 0 - joystickTilt;
        }
        joystickWobble = Math.abs(MPU_WOBBLE_SAMPLES.getHighest());
    }

    // ---------------------------------
// --------- SCREENSAVER -----------
// ---------------------------------
    void screenSaverTick() {
        int n, b, c, i;
        long mm = millis();
        int mode = (int) ((mm / 20000) % 2);

        for (i = 0; i < NUM_LEDS; i++) {
            LEDS[i].nscale8(250);
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
                }
            }
        } else if (mode == 1) {
            // Random flashes
            Random random = new Random(mm);
            for (i = 0; i < NUM_LEDS; i++) {
                if (random.nextInt(200) == 0) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    LEDS[i] = CHSV(25, 255, 100);
                }
            }
        }
    }

    void levelComplete() {
        stageStartTime = millis();
        stage = "WIN";
        if (levelNumber == LEVEL_COUNT) {
            stage = "COMPLETE";
        }
        lives = 3;
        updateLives();
    }

    boolean inLava(int pos) {
        // Returns if the player is in active lava
        int i;
        Lava LP;
        for (i = 0; i < LAVA_COUNT; i++) {
            LP = lavaPool[i];
            if (LP.isAlive() && LP.state.equals("ON")) {
                if (LP.left < pos && LP.right > pos) {
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
        for (int p = 0; p < PARTICLE_COUNT; p++) {
            particlePool[p].spawn(playerPosition);
        }
        stageStartTime = millis();
        stage = "DEAD";
        killTime = millis();
    }

    void tickConveyors() {
        int b, dir, n, i, ss, ee, led;
        long m = 10000 + millis();
        playerPositionModifier = 0;

        for (i = 0; i < CONVEYOR_COUNT; i++) {
            if (conveyorPool[i].isAlive()) {
                dir = conveyorPool[i].direction;
                ss = getLED(conveyorPool[i].startPoint);
                ee = getLED(conveyorPool[i].endPoint);
                for (led = ss; led < ee; led++) {
                    b = 5;
                    n = (int) ((-led + (m / 100)) % 5);
                    if (dir == -1) {
                        n = (int) ((led + (m / 100)) % 5);
                    }
                    b = (int) ((5 - n) / 2.0);
                    if (b > 0) {
                        // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
                        LEDS[led] = CRGB.create(0, 0, b);
                    }
                }

                if (playerPosition > conveyorPool[i].startPoint && playerPosition < conveyorPool[i].endPoint) {
                    if (dir == -1) {
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
        double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + offset;
    }

    private int constrain(int value, int lower, int upper) {
        return Math.max(Math.min(value, upper), lower);
    }

    void tickSpawners() {
        long mm = millis();
        for (int s = 0; s < SPAWN_COUNT; s++) {
            if (spawnPool[s].isAlive() && spawnPool[s].activate < mm) {
                if (spawnPool[s].lastSpawned + spawnPool[s].rate < mm || spawnPool[s].lastSpawned == 0) {
                    spawnEnemy(spawnPool[s].position, spawnPool[s].direction, spawnPool[s].sp, 0);
                    spawnPool[s].lastSpawned = mm;
                }
            }
        }
    }

    void tickBoss() {
        // DRAW
        if (boss.isAlive()) {
            boss.ticks++;
            for (int i = getLED(boss.position - BOSS_WIDTH / 2); i <= getLED(boss.position + BOSS_WIDTH / 2); i++) {
                LEDS[i] = CRGB.DarkRed;
                LEDS[i].mod(100);
            }
            // CHECK COLLISION
            if (getLED(playerPosition) > getLED(boss.position - BOSS_WIDTH / 2) && getLED(playerPosition) < getLED(boss.position + BOSS_WIDTH)) {
                die();
                return;
            }
            // CHECK FOR ATTACK
            if (attacking) {
                if (
                    (getLED(playerPosition + (ATTACK_WIDTH / 2)) >= getLED(boss.position - BOSS_WIDTH / 2) && getLED(playerPosition + (ATTACK_WIDTH / 2)) <= getLED(boss.position + BOSS_WIDTH / 2)) ||
                        (getLED(playerPosition - (ATTACK_WIDTH / 2)) <= getLED(boss.position + BOSS_WIDTH / 2) && getLED(playerPosition - (ATTACK_WIDTH / 2)) >= getLED(boss.position - BOSS_WIDTH / 2))
                    ) {
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
        Lava LP;
        for (i = 0; i < LAVA_COUNT; i++) {
            flicker = new Random().nextInt(5);
            LP = lavaPool[i];
            if (LP.isAlive()) {
                A = getLED(LP.left);
                B = getLED(LP.right);
                if (LP.state.equals("OFF")) {
                    if (LP.lastOn + LP.offtime < mm) {
                        LP.state = "ON";
                        LP.lastOn = mm;
                    }
                    for (p = A; p <= B; p++) {
                        LEDS[p] = CRGB.create(3 + flicker, (3 + flicker) / 1.5, 0);
                    }
                } else if (LP.state.equals("ON")) {
                    if (LP.lastOn + LP.ontime < mm) {
                        LP.state = "OFF";
                        LP.lastOn = mm;
                    }
                    for (p = A; p <= B; p++) {
                        LEDS[p] = CRGB.create(150 + flicker, 100 + flicker, 0);
                    }
                }
            }
            lavaPool[i] = LP;
        }
    }

    void tickEnemies() {
        for (int i = 0; i < ENEMY_COUNT; i++) {
            if (enemyPool[i].isAlive()) {
                enemyPool[i].tick(millis());
                // hit attack?
                if (attacking) {
                    if (enemyPool[i].position > playerPosition - (ATTACK_WIDTH / 2) && enemyPool[i].position < playerPosition + (ATTACK_WIDTH / 2)) {
                        enemyPool[i].kill();
                        SFXkill();
                    }
                }
                if (inLava(enemyPool[i].position)) {
                    enemyPool[i].kill();
                    SFXkill();
                }
                // Draw (if still alive)
                if (enemyPool[i].isAlive()) {
                    LEDS[getLED(enemyPool[i].position)] = CRGB.create(255, 0, 0); // TODO is create correct here? it was CRGB(255, 0, 0)
                }
                // hit player?
                if (
                    (enemyPool[i].playerSide == 1 && enemyPool[i].position <= playerPosition) ||
                        (enemyPool[i].playerSide == -1 && enemyPool[i].position >= playerPosition)
                    ) {
                    die();
                    return;
                }
            }
        }
    }

    private boolean tickParticles() {
        boolean stillActive = false;
        for (int p = 0; p < PARTICLE_COUNT; p++) {
            if (particlePool[p].isAlive()) {
                particlePool[p].tick(USE_GRAVITY);
//  TODO:       LEDS[getLED(particlePool[p].position)] += CRGB.(particlePool[p].power, 0, 0);
                stillActive = true;
            }
        }
        return stillActive;
    }

    private void drawPlayer() {
        LEDS[getLED(playerPosition)] = CRGB.create(0, 255, 0); // TODO
    }

    void drawAttack() {
        if (!attacking) {
            return;
        }
        // TODO check casts
        int n = (int) map((int) (millis() - attackMillis), 0, ATTACK_DURATION, 100, 5);
        for (int i = getLED(playerPosition - (ATTACK_WIDTH / 2)) + 1; i <= getLED(playerPosition + (ATTACK_WIDTH / 2)) - 1; i++) {
            LEDS[i] = CRGB.create(0, 0, n); // TODO
        }
        if (n > 90) {
            n = 255;
            LEDS[getLED(playerPosition)] = CRGB.create(255, 255, 255);// TODO
        } else {
            n = 0;
            LEDS[getLED(playerPosition)] = CRGB.create(0, 255, 0); // TODO
        }
        LEDS[getLED(playerPosition - (ATTACK_WIDTH / 2))] = CRGB.create(n, n, 255); // TODO
        LEDS[getLED(playerPosition + (ATTACK_WIDTH / 2))] = CRGB.create(n, n, 255);     // TODO
    }

    private void drawExit() {
        if (!boss.isAlive()) {
            LEDS[NUM_LEDS - 1] = CRGB.create(0, 0, 255); // TODO
        }
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
