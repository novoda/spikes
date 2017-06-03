package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class AndroidThingsActivity extends AppCompatActivity {

    private final SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();

    private GamePresenter gamePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        GameMvp.View gameView = (GameScreen) findViewById(R.id.game_screen);
        MidiKeyboardDriver midiKeyboardDriver = new MidiKeyboardDriver.KeyStationMini32(this);
        GameModel gameModel = new GameModel(new SongSequenceFactory(), simplePitchNotationFormatter, midiKeyboardDriver);
        gamePresenter = new GamePresenter(gameModel, gameView);

        gamePresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gamePresenter.onResume();
    }

    @Override
    protected void onPause() {
        gamePresenter.onPause();
        super.onPause();
    }

}
