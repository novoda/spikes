package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AndroidThingsActivity extends AppCompatActivity {

    private final SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();
    private MidiKeyboardDriver midiKeyboardDriver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        midiKeyboardDriver = new MidiKeyboardDriver.KeyStationMini32(this);
        midiKeyboardDriver.attachListener(new NoteListener() {

            @Override
            public void onPress(Note note) {
                Log.d("!!!", "Note pressed " + simplePitchNotationFormatter.format(note));
            }

            @Override
            public void onRelease(Note note) {
                Log.d("!!!", "Note released " + simplePitchNotationFormatter.format(note));
            }
        });
        midiKeyboardDriver.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        midiKeyboardDriver.close();
    }

}
