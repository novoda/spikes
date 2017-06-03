package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AndroidThingsActivity extends AppCompatActivity {

    private final SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();

    private GamePresenter gamePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        GameMvp.View gameView = (GameScreen) findViewById(R.id.game_screen);
        Piano virtualPiano = (Piano) findViewById(R.id.piano_view);
        Piano piano = new KeyStationMini32(this);
        GameModel gameModel = new GameModel(new SongSequenceFactory(), simplePitchNotationFormatter, virtualPiano);
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
