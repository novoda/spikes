package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int NUM_OF_SQUARES = 25;

    private DungeonCrawlerGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = new AndroidDeviceDisplay(this, findViewById(R.id.scrollView), NUM_OF_SQUARES);

        Screensaver screensaver = new Screensaver(display, NUM_OF_SQUARES);
        JoystickActuator joystickActuator = new AndroidViewJoystickActuator(findViewById(R.id.joystick));
        LogcatSoundEffectsPlayer soundEffectsPlayer = new LogcatSoundEffectsPlayer();
        ArduinoLoop looper = new ArduinoLoop();
        game = InitHack.newInstance(NUM_OF_SQUARES, display, this::updateLives, joystickActuator, soundEffectsPlayer, screensaver, looper);

        findViewById(R.id.button2).setOnClickListener(v -> {
            game.start();
            Log.d("TUT", "Game restarting");
        });
    }

    private void updateLives(int lives) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.lives_text_view)).setText("Lives " + lives));
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.start();
        Log.d("TUT", "Game starting");
    }

    @Override
    protected void onPause() {
        Log.d("TUT", "Game stopping");
        game.stop();
        super.onPause();
    }

}
