package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MainActivity extends Activity {

    // LED setup
    private static final int NUM_LEDS = 300;

    private static final String SPI_DEVICE_NAME = "SPI0.0";
    //    private static final Ws2801.Mode WS2801_MODE = Ws2801.Mode.RBG;
    private static final Apa102.Mode APA102_MODE = Apa102.Mode.RBG;

    private static final int[] lifeLEDs = new int[]{52, 50, 40};

    //    private Ws2801 ws2801;
    private Apa102 apa102;
    private DungeonCrawlerGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TUT", "Things game starting");

//        ws2801 = createWs2801();
        apa102 = createApa102();
//        Display ledStrip = new Ws2801Display(ws2801, NUM_LEDS);
        Display ledStrip = new Apa102Display(apa102, NUM_LEDS);
        JoystickActuator joystickActuator = new MPU6050JoystickActuator(MPU6050.create(PeripheralManager.getInstance()));
//        JoystickActuator joystickActuator = new FakeJoystickActuator();
        SpeakerSoundEffectsPlayer soundEffectsPlayer = new SpeakerSoundEffectsPlayer();
        Screensaver screensaver = new Screensaver(ledStrip, NUM_LEDS);
        ArduinoLoop looper = new ArduinoLoop();
        game = InitHack.newInstance(NUM_LEDS, ledStrip, this::updateLives, joystickActuator, soundEffectsPlayer, screensaver, looper);

        game.start();
        Log.d("TUT", "Things game started");
    }

    private Apa102 createApa102() {
        try {
            return new Apa102(SPI_DEVICE_NAME, APA102_MODE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create the Apa102 driver", e);
        }
    }

    // private Ws2801 createWs2801() {
    //     try {
    //         return Ws2801.create(SPI_DEVICE_NAME, WS2801_MODE);
    //     } catch (IOException e) {
    //         throw new IllegalStateException("Unable to create the Ws2801 driver", e);
    //     }
    // }

    private void updateLives(int lives) {
        for (int i = 0; i < lives; i++) {
            digitalWrite(lifeLEDs[i], lives > i ? Gpio.ACTIVE_HIGH : Gpio.ACTIVE_LOW);
        }
    }

    // TODO GPIO
    private void digitalWrite(int pin, int value) {
        Log.d("TUT", "Digital write pin " + pin + " value " + value);
    }

    @Override
    protected void onDestroy() {
        game.stop();
//        safeCloseWs2801();
        safeCloseApa102();
        super.onDestroy();
    }

    // private void safeCloseWs2801() {
    //     if (ws2801 != null) {
    //         try {
    //             ws2801.close();
    //         } catch (IOException e) {
    //             // ignore
    //         }
    //     }
    // }

    private void safeCloseApa102() {
        if (apa102 != null) {
            try {
                apa102.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
