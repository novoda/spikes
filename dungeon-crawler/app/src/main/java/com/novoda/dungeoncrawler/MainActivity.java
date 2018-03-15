package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    // MPU
    MPU6050 accelgyro;
    int ax, ay, az;
    int gx, gy, gz;

    // LED setup
    int NUM_LEDS = 475;
    int DATA_PIN = 3;
    int CLOCK_PIN = 4;
    int LED_COLOR_ORDER = 0;//BGR;//GBR
    int BRIGHTNESS = 150;
    int DIRECTION = 1;     // 0 = right to left, 1 = left to right
    int MIN_REDRAW_INTERVAL = 16;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps
    int USE_GRAVITY = 1;     // 0/1 use gravity (LED strip going up wall)
    int BEND_POINT = 550;   // 0/1000 point at which the LED strip goes up the wall

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
    char stage;                       // what stage the game is at (PLAY/DEAD/WIN/GAMEOVER)
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

        // Fast LED
//        FastLED.addLeds<APA102, DATA_PIN, CLOCK_PIN, LED_COLOR_ORDER> (leds, NUM_LEDS);
//        FastLED.setBrightness(BRIGHTNESS);
//        FastLED.setDither(1);

        // Life LEDs
//        for (int i = 0; i < 3; i++) {
//            pinMode(lifeLEDs[i], OUTPUT);
//            digitalWrite(lifeLEDs[i], HIGH);
//        }

//        loadLevel();
    }

}
