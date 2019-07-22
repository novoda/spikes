package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class MainActivity extends Activity implements RemoteGamePauseObservable.OnToggleListener {

    // LED setup
    private static final int NUM_LEDS = 300;

//        private static final String SPI_DEVICE_NAME = "SPI3.0"; // NXP
    private static final String SPI_DEVICE_NAME = "SPI0.0"; // RPi
    private static final Apa102.Mode APA102_MODE = Apa102.Mode.BGR;

    private static final int[] lifeLEDs = new int[]{52, 50, 40};

    private Apa102 apa102;
    private DungeonCrawlerGame game;
    private MPU6050 mpu6050;

    private RemoteGamePauseObservable remoteGamePauseObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apa102 = createApa102();
        Display ledStrip = new Apa102Display(apa102, NUM_LEDS);
        mpu6050 = MPU6050.create(PeripheralManager.getInstance());
        JoystickActuator joystickActuator = new MPU6050JoystickActuator(mpu6050);
        SpeakerSoundEffectsPlayer soundEffectsPlayer = new SpeakerSoundEffectsPlayer();
        Screensaver screensaver = new Screensaver(ledStrip, NUM_LEDS);
        ArduinoLoop looper = new ArduinoLoop();
        game = InitHack.newInstance(NUM_LEDS, ledStrip, this::updateLives, joystickActuator, soundEffectsPlayer, screensaver, looper);
        game.start();

        remoteGamePauseObservable = new RemoteGamePauseObservable(FirebaseDatabase.getInstance(), this);
        remoteGamePauseObservable.startObserving();
    }

    private Apa102 createApa102() {
        try {
            return new Apa102(SPI_DEVICE_NAME, APA102_MODE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create the Apa102 driver", e);
        }
    }

    private void updateLives(int lives) {
        for (int i = 0; i < lives; i++) {
            digitalWrite(lifeLEDs[i], lives > i ? Gpio.ACTIVE_HIGH : Gpio.ACTIVE_LOW);
        }
    }

    // TODO GPIO
    private void digitalWrite(int pin, int value) {
//        Log.d("TUT", "Digital write pin " + pin + " value " + value);
    }

    @Override
    public void onPauseGame() {
        game.pause();
    }

    @Override
    public void onResumeGame() {
        game.resume();
    }

    @Override
    protected void onDestroy() {
        remoteGamePauseObservable.stopObserving();
        game.stop();
        safeCloseApa102();
        super.onDestroy();
    }

    private void safeCloseApa102() {
        if (mpu6050 != null) {
            mpu6050.close();
        }
        if (apa102 != null) {
            try {
                apa102.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
