package com.novoda.pianohero;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

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
        final NoteEventDispatcher noteEventDispatcher = new NoteEventDispatcher(new NoteEventDispatcher.PlayListener() {
            @Override
            public void onPlayed(Notes notes) {
                Log.d("!!!", "user played: " + notes.toString());
            }
        });

        pianoView.attach(new PianoC4ToB5View.KeyListener() {
            @Override
            public void onClick(Note note) {
                noteEventDispatcher.onReceive(note);
            }
        });
    }

    private static class NoteEventDispatcher {

        private final PlayListener playListener;

        NoteEventDispatcher(PlayListener playListener) {
            this.playListener = playListener;
        }

        public void onReceive(Note note) {
            // TODO: it's not enough to collect onClicks because we need to detect when keys are pressed together
            // we should be able to reuse this for MIDI controller since that will also send individual events
            // Perhaps we can keep a buffer of note events and forward them on as `Notes` to the next layer

            playListener.onPlayed(new Notes(note));
        }

        public interface PlayListener {

            void onPlayed(Notes notes);
        }

    }

    @Override
    protected void onPause() {
        pianoView.detachKeyListener();
        super.onPause();
    }
}
