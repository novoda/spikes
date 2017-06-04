package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.view.View.GONE;

public class AndroidThingsActivity extends AppCompatActivity {

    private static final boolean HIDE_VIRTUAL_PIANO = true;

    private final SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();

    private GamePresenter gamePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        GameMvp.View gameView = (GameMvp.View) findViewById(R.id.game_screen);
        Piano piano = createPiano();
        GameModel gameModel = new GameModel(new SongSequenceFactory(), simplePitchNotationFormatter, piano);
        gamePresenter = new GamePresenter(gameModel, gameView);

        gamePresenter.onCreate();
    }

    private Piano createPiano() {
        C4ToB5ViewPiano virtualPianoView = (C4ToB5ViewPiano) findViewById(R.id.piano_view);
        if (HIDE_VIRTUAL_PIANO) {
            virtualPianoView.setVisibility(GONE);
            return new CompositePiano(new KeyStationMini32Piano(this));
        } else {
            return new CompositePiano(virtualPianoView, new KeyStationMini32Piano(this));
        }
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
