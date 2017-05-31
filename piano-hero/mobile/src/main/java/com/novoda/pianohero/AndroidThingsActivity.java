package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class AndroidThingsActivity extends AppCompatActivity {

    private final SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();

    private MidiKeyboardDriver midiKeyboardDriver;
    private GamePresenter gamePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        TextView statusTextView = (TextView) findViewById(R.id.status_text_view);
        TrebleStaffWidget trebleStaffWidget = (TrebleStaffWidget) findViewById(R.id.treble_staff_widget);
        GameMvp.View gameView = new AndroidThingsView(statusTextView, trebleStaffWidget);

        GameModel gameModel = new GameModel(new SongSequenceFactory(), simplePitchNotationFormatter);

        gamePresenter = new GamePresenter(gameModel, gameView);
        midiKeyboardDriver = new MidiKeyboardDriver.KeyStationMini32(this);
        midiKeyboardDriver.attachListener(new NoteListener() {

            @Override
            public void onPlay(Note note) {
                gamePresenter.onNotePlayed(note);
            }
        });

        gamePresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiKeyboardDriver.open();
    }

    @Override
    protected void onPause() {
        midiKeyboardDriver.close();
        super.onPause();
    }

}
