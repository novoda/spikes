package com.novoda.pianohero;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

public class DemoMainActivity extends Activity {

    private PianoC4ToB5View pianoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        pianoView = (PianoC4ToB5View) findViewById(R.id.piano_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pianoView.attach(new PianoC4ToB5View.KeyListener() {
            @Override
            public void onClick(Note note) {
                Log.d("!!!", "onClick: " + note.midi());

            }
        });
    }

    @Override
    protected void onPause() {
        pianoView.detachKeyListener();
        super.onPause();
    }
}
