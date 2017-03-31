package com.novoda.pianohero;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

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
        final NotesPlayedDispatcher notesPlayedDispatcher = new NotesPlayedDispatcher(new NotesPlayedDispatcher.PlayListener() {
            @Override
            public void onPlayed(Notes notes) {
                Log.d("!!!", "user played: " + notes.toString());
            }
        });

        pianoView.attach(new PianoC4ToB5View.KeyListener() {
            @Override
            public void onPress(Note note) {
                notesPlayedDispatcher.registerNoteOn(note);
            }

            @Override
            public void onRelease(Note note) {
                notesPlayedDispatcher.registerNoteOff(note);
            }
        });
    }

    @Override
    protected void onPause() {
        pianoView.detachKeyListener();
        super.onPause();
    }

    private static class NotesPlayedDispatcher {

        private final Set<Note> notesOn = new HashSet<>();

        private final PlayListener playListener;

        NotesPlayedDispatcher(PlayListener playListener) {
            this.playListener = playListener;
        }

        public void registerNoteOn(Note note) {
            notesOn.add(note);
            dispatchNotesPlayed();
        }

        public void registerNoteOff(Note note) {
            notesOn.remove(note);
            dispatchNotesPlayed();
        }

        private void dispatchNotesPlayed() {
            playListener.onPlayed(new Notes(notesOn));
        }

        public interface PlayListener {

            void onPlayed(Notes notes);
        }
    }
}
