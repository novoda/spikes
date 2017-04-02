package com.novoda.pianohero;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.HashSet;
import java.util.Set;

public class PianoC4ToB5Activity extends AppCompatActivity {

    private Brain brain;
    private SimpleNotesOutputView outputView;
    private PianoC4ToB5View inputView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_c4_to_b5);

        outputView = (SimpleNotesOutputView) findViewById(R.id.simple_notes_output_view);
        inputView = (PianoC4ToB5View) findViewById(R.id.piano_view);
        brain = new Brain(onSequenceUpdatedCallback);

        startNewGame();
    }

    private final OnSequenceUpdatedCallback onSequenceUpdatedCallback = new OnSequenceUpdatedCallback() {
        @Override
        public void onNext(Sequence sequence) {
            outputView.display(sequence);
        }
    };

    private void startNewGame() {
        Sequence sequence = new SongSequenceFactory().maryHadALittleLamb();
        brain.start(sequence);
    }

    @Override
    protected void onResume() {
        super.onResume();
        brain.attach(new Brain.Callback() {
            @Override
            public void onSequenceComplete() {
                PianoHeroApplication.popToast("game complete, another!");
                startNewGame();
            }
        })
        ;
        inputView.attach(new NotesPlayedDispatcher(new NotesPlayedDispatcher.PlayListener() {
            @Override
            public void onPlayed(Notes notes) {
                brain.onNotesPlayed(notes);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.piano_c4_to_b5_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.restart_game) {
            startNewGame();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        brain.removeCallbacks();
        inputView.detachKeyListener();
        super.onPause();
    }

    private static class NotesPlayedDispatcher implements KeyListener {

        private final Set<Note> notesOn = new HashSet<>();

        private final PlayListener playListener;

        NotesPlayedDispatcher(PlayListener playListener) {
            this.playListener = playListener;
        }

        @Override
        public void onPress(Note note) {
            notesOn.add(note);
            dispatchNotesPlayed();
        }

        @Override
        public void onRelease(Note note) {
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
