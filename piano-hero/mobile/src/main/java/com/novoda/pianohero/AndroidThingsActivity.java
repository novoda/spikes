package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AndroidThingsActivity extends AppCompatActivity {

    private MidiKeyboardDriver midiKeyboardDriver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");

        midiKeyboardDriver = new MidiKeyboardDriver.KeyStationMini32(this);
        midiKeyboardDriver.attachListener(new MidiKeyboardDriver.KeyListener() {

            @Override
            public void onKeyPressed(MidiKeyboardDriver.Note note) {
                Log.d("!!!", "note pressed " + note);
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
