package com.novoda.dungeoncrawler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements RemoteGamePauseObservable.OnToggleListener {

    private static final int NUM_OF_SQUARES = 100;

    private DungeonCrawlerGame game;
    private RemoteGamePauseObservable remoteGamePauseObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = new AndroidDeviceDisplay(this, findViewById(R.id.leds_container), NUM_OF_SQUARES);

        Screensaver screensaver = new Screensaver(display, NUM_OF_SQUARES);
        JoystickActuator joystickActuator = new AndroidViewJoystickActuator(findViewById(R.id.joystick));
        LogcatSoundEffectsPlayer soundEffectsPlayer = new LogcatSoundEffectsPlayer();
        ArduinoLoop looper = new ArduinoLoop();
        game = InitHack.newInstance(NUM_OF_SQUARES, display, this::updateLives, joystickActuator, soundEffectsPlayer, screensaver, looper);

        findViewById(R.id.restart).setOnClickListener(v -> {
            game.start();
            Log.d("TUT", "Game restarting");
        });

        Log.d("TUT", "Game starting");

        remoteGamePauseObservable = new RemoteGamePauseObservable(FirebaseDatabase.getInstance(), this);
        remoteGamePauseObservable.startObserving();
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
    public void onPauseGame() {
        Toast.makeText(this, "Game Paused", Toast.LENGTH_SHORT).show();
        game.pause();
    }

    @Override
    public void onResumeGame() {
        game.resume();
        Toast.makeText(this, "Game Resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        Log.d("TUT", "Game stopping");
        game.stop();
        remoteGamePauseObservable.stopObserving();
        super.onPause();
    }

}
