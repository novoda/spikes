package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;

import java.util.Random;

public class MainActivity extends Activity {

    // MPU
    MPU6050 accelgyro;
    int ax, ay, az;
    int gx, gy, gz;

    // LED setup
    final int NUM_LEDS = 475;
    final int DATA_PIN = 3;
    final int CLOCK_PIN = 4;
    final int LED_COLOR_ORDER = 0;//BGR;//GBR
    final int BRIGHTNESS = 150;
    final int DIRECTION = 1;     // 0 = right to left, 1 = left to right
    final int MIN_REDRAW_INTERVAL = 16;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps
    final int USE_GRAVITY = 1;     // 0/1 use gravity (LED strip going up wall)
    final int BEND_POINT = 550;   // 0/1000 point at which the LED strip goes up the wall

    // GAME
    long previousMillis = 0;           // Time of the last redraw
    int levelNumber = 0;
    long lastInputTime = 0;
    int TIMEOUT = 30000;
    int LEVEL_COUNT = 9;
    int MAX_VOLUME = 10;
    iSin isin = new iSin();

    // JOYSTICK
    int JOYSTICK_ORIENTATION = 1;     // 0, 1 or 2 to set the angle of the joystick
    int JOYSTICK_DIRECTION = 1;     // 0/1 to flip joystick direction
    int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack
    int JOYSTICK_DEADZONE = 5;     // Angle to ignore
    int joystickTilt = 0;              // Stores the angle of the joystick
    int joystickWobble = 0;            // Stores the max amount of acceleration (wobble)

    // WOBBLE ATTACK
    int ATTACK_WIDTH = 70;     // Width of the wobble attack, world is 1000 wide
    int ATTACK_DURATION = 500;    // Duration of a wobble attack (ms)
    long attackMillis = 0;             // Time the attack started
    boolean attacking = false;                // Is the attack in progress?
    int BOSS_WIDTH = 40;

    // PLAYER
    int MAX_PLAYER_SPEED = 10;     // Max move speed of the player
    String stage;                       // what stage the game is at (PLAY/DEAD/WIN/GAMEOVER)
    long stageStartTime;               // Stores the time the stage changed for stages that are time based
    int playerPosition;                // Stores the player position
    int playerPositionModifier;        // +/- adjustment to player position
    boolean playerAlive;
    long killTime;
    int lives = 3;

    // POOLS
    int[] lifeLEDs = new int[]{52, 50, 40};

    Enemy[] enemyPool = new Enemy[]{
            new Enemy(), new Enemy(), new Enemy(), new Enemy(), new Enemy(),
            new Enemy(), new Enemy(), new Enemy(), new Enemy(), new Enemy()
    };

    private static final int enemyCount = 10;
    Particle[] particlePool = {
            new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle(), new Particle()
    };
    private static final int particleCount = 40;
    Spawner[] spawnPool = {
            new Spawner(), new Spawner()
    };
    private static final int spawnCount = 2;
    Lava[] lavaPool = {
            new Lava(), new Lava(), new Lava(), new Lava()
    };
    private static final int lavaCount = 4;
    Conveyor[] conveyorPool = {
            new Conveyor(), new Conveyor()
    };
    private static final int conveyorCount = 2;
    Boss boss = new Boss();

    CRGB[] leds = new CRGB[NUM_LEDS];
    RunningMedian MPUAngleSamples = new RunningMedian(5);
    RunningMedian MPUWobbleSamples = new RunningMedian(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelgyro.initialize();

//         Fast LED
//        FastLED.addLeds<APA102, DATA_PIN, CLOCK_PIN, LED_COLOR_ORDER> (leds, NUM_LEDS);
//        FastLED.setBrightness(BRIGHTNESS);
//        FastLED.setDither(1);

        // Life LEDs
//        for (int i = 0; i < 3; i++) {
//            pinMode(lifeLEDs[i], OUTPUT);
//            digitalWrite(lifeLEDs[i], Gpio.ACTIVE_HIGH);
//        }

        loadLevel();
        // loop()
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
                spawnPool[0].Spawn(1000, 3000, 2, 0, 0);
                break;
            case 3:
                // Lava intro
                spawnLava(400, 490, 2000, 2000, 0, "OFF");
                spawnPool[0].Spawn(1000, 5500, 3, 0, 0);
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
                spawnPool[0].Spawn(1000, 3800, 4, 0, 0);
                break;
            case 8:
                // Sin enemy #2
                spawnEnemy(700, 1, 7, 275);
                spawnEnemy(500, 1, 5, 250);
                spawnPool[0].Spawn(1000, 5500, 4, 0, 3000);
                spawnPool[1].Spawn(0, 5500, 5, 1, 10000);
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
        for (int i = 0; i < enemyCount; i++) {
            enemyPool[i].kill();
        }
        for (int i = 0; i < particleCount; i++) {
            particlePool[i].kill();
        }
        for (int i = 0; i < spawnCount; i++) {
            spawnPool[i].kill();
        }
        for (int i = 0; i < lavaCount; i++) {
            lavaPool[i].kill();
        }
        for (int i = 0; i < conveyorCount; i++) {
            conveyorPool[i].kill();
        }
        boss.kill();
    }

    void spawnEnemy(int pos, int dir, int sp, int wobble) {
        for (int e = 0; e < enemyCount; e++) {
            if (!enemyPool[e].Alive()) {
                enemyPool[e].Spawn(pos, dir, sp, wobble);
                enemyPool[e].playerSide = pos > playerPosition ? 1 : -1;
                return;
            }
        }
    }

    void spawnLava(int left, int right, int ontime, int offtime, int offset, String state) {
        for (int i = 0; i < lavaCount; i++) {
            if (!lavaPool[i].Alive()) {
                lavaPool[i].Spawn(left, right, ontime, offtime, offset, state);
                return;
            }
        }
    }

    void spawnConveyor(int startPoint, int endPoint, int dir) {
        for (int i = 0; i < conveyorCount; i++) {
            if (!conveyorPool[i].Alive()) {
                conveyorPool[i].Spawn(startPoint, endPoint, dir);
                return;
            }
        }
    }

    void spawnBoss() {
        boss.Spawn();
        moveBoss();
    }

    void moveBoss() {
        int spawnSpeed = 2500;
        if (boss._lives == 2) {
            spawnSpeed = 2000;
        }
        if (boss._lives == 1) {
            spawnSpeed = 1500;
        }
        spawnPool[0].Spawn(boss._pos, spawnSpeed, 3, 0, 0);
        spawnPool[1].Spawn(boss._pos, spawnSpeed, 3, 1, 0);
    }

    /**
     * After creating a setup() function, which initializes and sets the initial values, the loop() function does precisely what its name suggests, and loops consecutively, allowing your program to change and respond. Use it to actively control the Arduino board.
     * https://www.arduino.cc/en/Reference/Loop/
     * TODO Make this loop
     */
    public void loop() {
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
            } else if (stage == "PLAY") {
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
                    if (playerPosition >= 1000 && !boss.Alive()) {
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
                    int n = Math.max(map(((mm - stageStartTime)), 0, 500, NUM_LEDS, 0), 0);
                    for (int i = NUM_LEDS; i >= n; i--) {
                        brightness = 255;
                        leds[i] = CRGB.create(0, brightness, 0); // TODO CRGB(0, brightness, 0)
                    }
                    SFXwin();
                } else if (stageStartTime + 1000 > mm) {
                    int n = Math.max(map(((mm - stageStartTime)), 500, 1000, NUM_LEDS, 0), 0);
                    for (int i = 0; i < n; i++) {
                        brightness = 255;
                        leds[i] = CRGB(0, brightness, 0);
                    }
                    SFXwin();
                } else if (stageStartTime + 1200 > mm) {
                    leds[0] = CRGB(0, 255, 0);
                } else {
                    nextLevel();
                }
            } else if (stage.equals("COMPLETE")) {
//                FastLED.clear();  TODO
                SFXcomplete();
                if (stageStartTime + 500 > mm) {
                    int n = Math.max(map(((mm - stageStartTime)), 0, 500, NUM_LEDS, 0), 0);
                    for (int i = NUM_LEDS; i >= n; i--) {
                        brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        leds[i].setHSV(brightness, 255, 50);
                    }
                } else if (stageStartTime + 5000 > mm) {
                    for (int i = NUM_LEDS; i >= 0; i--) {
                        brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        leds[i].setHSV(brightness, 255, 50);
                    }
                } else if (stageStartTime + 5500 > mm) {
                    int n = Math.max(map(((mm - stageStartTime)), 5000, 5500, NUM_LEDS, 0), 0);
                    for (int i = 0; i < n; i++) {
                        brightness = (int) ((Math.sin(((i * 10) + mm) / 500.0) + 1) * 255);
                        leds[i].setHSV(brightness, 255, 50);
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

        // TODO modify values
        accelgyro.getMotion6(ax, ay, az, gx, gy, gz);
        int a = (JOYSTICK_ORIENTATION == 0 ? ax : (JOYSTICK_ORIENTATION == 1 ? ay : az)) / 166;
        int g = (JOYSTICK_ORIENTATION == 0 ? gx : (JOYSTICK_ORIENTATION == 1 ? gy : gz));
        if (Math.abs(a) < JOYSTICK_DEADZONE) {
            a = 0;
        }
        if (a > 0) {
            a -= JOYSTICK_DEADZONE;
        }
        if (a < 0) {
            a += JOYSTICK_DEADZONE;
        }
        MPUAngleSamples.add(a);
        MPUWobbleSamples.add(g);

        joystickTilt = MPUAngleSamples.getMedian();
        if (JOYSTICK_DIRECTION == 1) {
            joystickTilt = 0 - joystickTilt;
        }
        joystickWobble = Math.abs(MPUWobbleSamples.getHighest());
    }

    // ---------------------------------
// --------- SCREENSAVER -----------
// ---------------------------------
    void screenSaverTick() {
        int n, b, c, i;
        long mm = millis();
        int mode = (int) ((mm / 20000) % 2);

        for (i = 0; i < NUM_LEDS; i++) {
            leds[i].nscale8(250);
        }
        if (mode == 0) {
            // Marching green <> orange
            n = (int) ((mm / 250) % 10);
            b = (int) (10 + ((Math.sin(mm / 500.00) + 1) * 20.00));
            c = (int) (20 + ((Math.sin(mm / 5000.00) + 1) * 33));
            for (i = 0; i < NUM_LEDS; i++) {
                if (i % 10 == n) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    leds[i] = CHSV(c, 255, 150);
                }
            }
        } else if (mode == 1) {
            // Random flashes
            Random random = new Random(mm);
            for (i = 0; i < NUM_LEDS; i++) {
                if (random.nextInt(200) == 0) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    leds[i] = CHSV(25, 255, 100);
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
        for (i = 0; i < lavaCount; i++) {
            LP = lavaPool[i];
            if (LP.Alive() && LP._state.equals("ON")) {
                if (LP._left < pos && LP._right > pos) {
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
        for (int p = 0; p < particleCount; p++) {
            particlePool[p].Spawn(playerPosition);
        }
        stageStartTime = millis();
        stage = "DEAD";
        killTime = millis();
    }

    void tickConveyors() {
        int b, dir, n, i, ss, ee, led;
        long m = 10000 + millis();
        playerPositionModifier = 0;

        for (i = 0; i < conveyorCount; i++) {
            if (conveyorPool[i]._alive) {
                dir = conveyorPool[i]._dir;
                ss = getLED(conveyorPool[i]._startPoint);
                ee = getLED(conveyorPool[i]._endPoint);
                for (led = ss; led < ee; led++) {
                    b = 5;
                    n = (int) ((-led + (m / 100)) % 5);
                    if (dir == -1) {
                        n = (int) ((led + (m / 100)) % 5);
                    }
                    b = (int) ((5 - n) / 2.0);
                    if (b > 0) {
                        // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                        leds[led] = CRGB(0, 0, b);
                    }
                }

                if (playerPosition > conveyorPool[i]._startPoint && playerPosition < conveyorPool[i]._endPoint) {
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
        for (int s = 0; s < spawnCount; s++) {
            if (spawnPool[s].Alive() && spawnPool[s]._activate < mm) {
                if (spawnPool[s]._lastSpawned + spawnPool[s]._rate < mm || spawnPool[s]._lastSpawned == 0) {
                    spawnEnemy(spawnPool[s]._pos, spawnPool[s]._dir, spawnPool[s]._sp, 0);
                    spawnPool[s]._lastSpawned = mm;
                }
            }
        }
    }

    void tickBoss() {
        // DRAW
        if (boss.Alive()) {
            boss._ticks++;
            for (int i = getLED(boss._pos - BOSS_WIDTH / 2); i <= getLED(boss._pos + BOSS_WIDTH / 2); i++) {
                leds[i] = CRGB.DarkRed;
                leds[i].mod(100);
            }
            // CHECK COLLISION
            if (getLED(playerPosition) > getLED(boss._pos - BOSS_WIDTH / 2) && getLED(playerPosition) < getLED(boss._pos + BOSS_WIDTH)) {
                die();
                return;
            }
            // CHECK FOR ATTACK
            if (attacking) {
                if (
                        (getLED(playerPosition + (ATTACK_WIDTH / 2)) >= getLED(boss._pos - BOSS_WIDTH / 2) && getLED(playerPosition + (ATTACK_WIDTH / 2)) <= getLED(boss._pos + BOSS_WIDTH / 2)) ||
                                (getLED(playerPosition - (ATTACK_WIDTH / 2)) <= getLED(boss._pos + BOSS_WIDTH / 2) && getLED(playerPosition - (ATTACK_WIDTH / 2)) >= getLED(boss._pos - BOSS_WIDTH / 2))
                        ) {
                    boss.Hit();
                    if (boss.Alive()) {
                        moveBoss();
                    } else {
                        spawnPool[0].Kill();
                        spawnPool[1].Kill();
                    }
                }
            }
        }
    }

    void tickLava() {
        int A, B, p, i, brightness, flicker;
        long mm = millis();
        Lava LP;
        for (i = 0; i < lavaCount; i++) {
            flicker = new Random().nextInt(5);
            LP = lavaPool[i];
            if (LP.Alive()) {
                A = getLED(LP._left);
                B = getLED(LP._right);
                if (LP._state.equals("OFF")) {
                    if (LP._lastOn + LP._offtime < mm) {
                        LP._state = "ON";
                        LP._lastOn = mm;
                    }
                    for (p = A; p <= B; p++) {
                        leds[p] = CRGB.create(3 + flicker, (3 + flicker) / 1.5, 0);
                    }
                } else if (LP._state.equals("ON")) {
                    if (LP._lastOn + LP._ontime < mm) {
                        LP._state = "OFF";
                        LP._lastOn = mm;
                    }
                    for (p = A; p <= B; p++) {
                        leds[p] = CRGB.create(150 + flicker, 100 + flicker, 0);
                    }
                }
            }
            lavaPool[i] = LP;
        }
    }

    void tickEnemies() {
        for (int i = 0; i < enemyCount; i++) {
            if (enemyPool[i].Alive()) {
                enemyPool[i].Tick();
                // Hit attack?
                if (attacking) {
                    if (enemyPool[i]._pos > playerPosition - (ATTACK_WIDTH / 2) && enemyPool[i]._pos < playerPosition + (ATTACK_WIDTH / 2)) {
                        enemyPool[i].Kill();
                        SFXkill();
                    }
                }
                if (inLava(enemyPool[i]._pos)) {
                    enemyPool[i].Kill();
                    SFXkill();
                }
                // Draw (if still alive)
                if (enemyPool[i].Alive()) {
                    leds[getLED(enemyPool[i]._pos)] = CRGB.create(255, 0, 0); // TODO is create correct here? it was CRGB(255, 0, 0)
                }
                // Hit player?
                if (
                        (enemyPool[i].playerSide == 1 && enemyPool[i]._pos <= playerPosition) ||
                                (enemyPool[i].playerSide == -1 && enemyPool[i]._pos >= playerPosition)
                        ) {
                    die();
                    return;
                }
            }
        }
    }

}
