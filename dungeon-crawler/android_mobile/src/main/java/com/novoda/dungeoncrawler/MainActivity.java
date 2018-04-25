package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.xrigau.driver.ws2801.Ws2801;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends Activity {

    // LED setup
    private static final int NUM_LEDS = 300;//475;
    private static final int DATA_PIN = 3;
    private static final int CLOCK_PIN = 4;
    private static final int LED_COLOR_ORDER = 0;//BGR;//GBR
    private static final int BRIGHTNESS = 150;
    private static final Direction DIRECTION = Direction.LEFT_TO_RIGHT;
    private static final int BEND_POINT = 550;   // 0/1000 point at which the LED strip goes up the wall // TODO not used

    private static final String SPI_DEVICE_NAME = "SPI0.0";
    private static final Ws2801.Mode WS2801_MODE = Ws2801.Mode.RBG;

    // GAME
    private static final int MAX_VOLUME = 10;

    // WOBBLE ATTACK
    static final int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack // TODO DOESN'T BELONG HERE

    // PLAYER
    private static final Display.CRGB PLAYER_COLOR = Display.CRGB.GREEN;

    // POOLS
    private static final int[] lifeLEDs = new int[]{52, 50, 40};

    private ArduinoLoop arduinoLoop = new ArduinoLoop();
    private Display ledStrip;
    private JoystickActuator joystickActuator;
    private JoystickActuator.JoyState joyState;
    private Ws2801 ws2801;
    private GameEngine gameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameEngine = new GameEngine(
                this::SFXattacking,
                this::SFXkill,
                this::SFXtilt,
                this::SFXdead,
                this::onWin,
                this::screenSaverTick,
                this::onCompleteGame,
                this::onGameOver,
                drawCallback
        );

//        joystickActuator = new MPU6050JoystickActuator(MPU6050.create(PeripheralManager.getInstance()));
        joystickActuator = new AndroidViewJoystickActuator(findViewById(R.id.joystick));

//         Fast LED
//        ledStrip = new FastLED(NUM_LEDS, LED_COLOR_ORDER, DATA_PIN, CLOCK_PIN);
//        ledStrip = new LogcatDisplay(NUM_LEDS);
        ws2801 = createWs2801();
        ledStrip = new Ws2801Display(ws2801, NUM_LEDS);
//        ledStrip = new AndroidDeviceDisplay(this, findViewById(R.id.scrollView), NUM_LEDS);
//        ledStrip.setBrightness(BRIGHTNESS);
//        ledStrip.setDither(1);

        // Life LEDs
        for (int i = 0; i < 3; i++) {
//            pinMode(lifeLEDs[i], OUTPUT);
//            digitalWrite(lifeLEDs[i], Gpio.ACTIVE_HIGH);
        }

        findViewById(R.id.button2).setOnClickListener(v -> {
            loadLevel();
        });

        loadLevel();

        arduinoLoop.start(gameEngine::loop);
    }

    private Ws2801 createWs2801() {
        try {
            return Ws2801.create(SPI_DEVICE_NAME, WS2801_MODE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create the Ws2801 driver", e);
        }
    }

    // ---------------------------------
// ------------ LEVELS -------------
// ---------------------------------
    private void loadLevel() {
        Log.d("TUT", "Game Starting");
        updateLives(3);
        gameEngine.loadLevel();
    }

    private void updateLives(int lives) {
        // Updates the life LEDs to show how many lives the player has left
        for (int i = 0; i < lives; i++) {
//            digitalWrite(lifeLEDs[i], lives > i ? Gpio.ACTIVE_HIGH : Gpio.ACTIVE_LOW);
        }
    }

    // TODO GPIO
    private void digitalWrite(int pin, int value) {
        Log.d("TUT", "Digital write pin " + pin + " value " + value);
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

    void onWin(long levelStartTime, long levelCurrentTime) {
        // LEVEL COMPLETE
        ledStrip.clear();
        if (levelStartTime + 500 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 0, 500, NUM_LEDS, 0), 0);
            for (int i = NUM_LEDS - 1; i >= n; i--) {
                ledStrip.set(i, PLAYER_COLOR);
            }
        } else if (levelStartTime + 1000 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 500, 1000, NUM_LEDS, 0), 0);
            for (int i = 0; i < n; i++) {
                ledStrip.set(i, PLAYER_COLOR);
            }
        } else if (levelStartTime + 1200 > levelCurrentTime) {
            ledStrip.set(0, PLAYER_COLOR);
        }
    }

    void onCompleteGame(long levelStartTime, long levelCurrentTime) {
        ledStrip.clear();
        SFXcomplete();
        if (levelStartTime + 500 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 0, 500, NUM_LEDS, 0), 0);
            for (int i = NUM_LEDS; i >= n; i--) {
                int brightness = (int) ((Math.sin(((i * 10) + levelCurrentTime) / 500.0) + 1) * 255);
                ledStrip.modifyHSV(i, brightness, 255, 50);
            }
        } else if (levelStartTime + 5000 > levelCurrentTime) {
            for (int i = NUM_LEDS; i >= 0; i--) {
                int brightness = (int) ((Math.sin(((i * 10) + levelCurrentTime) / 500.0) + 1) * 255);
                ledStrip.modifyHSV(i, brightness, 255, 50);
            }
        } else if (levelStartTime + 5500 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 5000, 5500, NUM_LEDS, 0), 0);
            for (int i = 0; i < n; i++) {
                int brightness = (int) ((Math.sin(((i * 10) + levelCurrentTime) / 500.0) + 1) * 255);
                ledStrip.modifyHSV(i, brightness, 255, 50);
            }
        }
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
        joyState = joystickActuator.getInput();
    }

    int getLED(int pos) {
        // The world is 1000 pixels wide, this converts world units into an LED number
        return constrain((int) GameEngine.map(pos, 0, 1000, 0, NUM_LEDS - 1), 0, NUM_LEDS - 1);
    }

    private int constrain(int value, int lower, int upper) {
        return Math.max(Math.min(value, upper), lower);
    }

    private final GameEngine.DrawCallback drawCallback = new GameEngine.DrawCallback() {
        @Override
        public void startDraw() {
            ledStrip.clear();
        }

        @Override
        public void drawPlayer(int position) {
            MainActivity.this.drawPlayer(position);
        }

        @Override
        public void drawConveyor(int startPoint, int endPoint, Direction direction, long frame) {
            int startPosition = getLED(startPoint);
            int endPosition = getLED(endPoint);
            for (int led = startPosition; led < endPosition; led++) {
                int n;
                if (direction == Direction.LEFT_TO_RIGHT) {
                    n = (int) ((led + (frame / 100)) % 5);
                } else {
                    n = (int) ((-led + (frame / 100)) % 5);
                }
                int blue = (int) ((5 - n) / 2.0);
                if (blue > 0) {
                    ledStrip.set(led, new Display.CRGB(0, 0, blue));
                }
            }
        }

        @Override
        public void drawAttack(int startPoint, int centerPoint, int endPoint, int attackPower) {
            MainActivity.this.drawAttack(startPoint, centerPoint, endPoint, attackPower);
        }

        @Override
        public void drawParticle(int position, int power) {
            //  TODO: LEDS[getLED(position)] += CRGB.(power, 0, 0);
        }

        @Override
        public void drawEnemy(int position) {
            int p = getLED(position);
            ledStrip.set(p, Display.CRGB.RED);
        }

        @Override
        public void drawLava(int lavaStartPosition, int lavaEndPosition, boolean enabled) {
            int A = getLED(lavaStartPosition);
            int B = getLED(lavaEndPosition);
            int flicker = new Random().nextInt(5);
            if(enabled) {
                for (int p = A; p <= B; p++) {
                    ledStrip.set(p, new Display.CRGB(150 + flicker, 100 + flicker, 0));
                }
            } else {
                for (int p = A; p <= B; p++) {
                    ledStrip.set(p, new Display.CRGB(3 + flicker, (int) ((3 + flicker) / 1.5), 0));
                }
            }
        }

        @Override
        public void drawBoss(int startPosition, int endPosition) {
            for (int i = getLED(startPosition); i <= getLED(endPosition); i++) {
                ledStrip.set(i, Display.CRGB.DARK_RED);
                ledStrip.modifyMod(i, 100);
            }
        }

        @Override
        public void drawExit() {
            MainActivity.this.drawExit();
        }

        @Override
        public void drawLives(int lives) {
            updateLives(lives);
        }

        @Override
        public void finishDraw() {
//            Log.d("TUT", "" + (millis() - frameTime));
//            Log.d("TUT", " - ");
            ledStrip.show();
//            Log.d("TUT", "" + (millis() - frameTime));
        }
    };

    private void drawPlayer(int playerPosition) {
        int p = getLED(playerPosition);
        ledStrip.set(p, PLAYER_COLOR);
    }

    void drawAttack(int startPoint, int centerPoint, int endPoint, int attackPower) {
        for (int i = getLED(endPoint) + 1; i <= getLED(startPoint) - 1; i++) {
            ledStrip.set(i, new Display.CRGB(0, 0, attackPower));
        }
        int i = getLED(centerPoint);
        if (attackPower > 90) {
            attackPower = 255;
            ledStrip.set(i, Display.CRGB.WHITE);
        } else {
            attackPower = 0;
            ledStrip.set(i, Display.CRGB.GREEN);
        }
        ledStrip.set(getLED(endPoint), new Display.CRGB(attackPower, attackPower, 255));
        ledStrip.set(getLED(startPoint), new Display.CRGB(attackPower, attackPower, 255));
    }

    private void drawExit() {
        int exitPosition = NUM_LEDS - 1;
        ledStrip.set(exitPosition, new Display.CRGB(0, 0, 255));
    }

    private void onGameOver() {
        ledStrip.clear();
    }

    @Override
    protected void onDestroy() {
        arduinoLoop.stop();
        safeCloseWs2801();
        super.onDestroy();
    }

    private void safeCloseWs2801() {
        if (ws2801 != null) {
            try {
                ws2801.close();
            } catch (IOException e) {
//                 ignore
            }
        }
    }

    // ---------------------------------
    // --------- SCREENSAVER -----------
    // ---------------------------------
    private void screenSaverTick(long frameTime) {
        ledStrip.clear(); // TODO I added this
        int n, b, c, i;
        int mode = (int) ((frameTime / 20000) % 2);

        for (i = 0; i < NUM_LEDS; i++) {
            ledStrip.modifyScale(i, 250);
        }
        if (mode == 0) {
            // Marching green <> orange
            n = (int) ((frameTime / 250) % 10);
            b = (int) (10 + ((Math.sin(frameTime / 500.00) + 1) * 20.00));
            c = (int) (20 + ((Math.sin(frameTime / 5000.00) + 1) * 33));
            for (i = 0; i < NUM_LEDS; i++) {
                if (i % 10 == n) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    LEDS[i] = CHSV(c, 255, 150);
                    ledStrip.set(i, new Display.CHSV(c, 255, 150));
                }
            }
        } else if (mode == 1) {
            // Random flashes
            Random random = new Random(frameTime);
            for (i = 0; i < NUM_LEDS; i++) {
                if (random.nextInt(200) == 0) {
                    // TODO https://github.com/FastLED/FastLED/wiki/Pixel-reference#chsv
//                    LEDS[i] = CHSV(25, 255, 100);
                    ledStrip.set(i, new Display.CHSV(25, 255, 100));
                }
            }
        }
    }
}
