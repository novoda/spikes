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

        TextView statusTextView = (TextView) findViewById(R.id.status_text_view);
        C4ToB5TrebleStaffWidget c4ToB5TrebleStaffWidget = (C4ToB5TrebleStaffWidget) findViewById(R.id.treble_staff_widget);
        GameMvp.View gameView = new AndroidThingsView(statusTextView, c4ToB5TrebleStaffWidget);

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
