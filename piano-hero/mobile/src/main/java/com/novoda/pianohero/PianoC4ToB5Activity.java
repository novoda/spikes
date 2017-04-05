package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Set;

public class PianoC4ToB5Activity extends AppCompatActivity {

    private PianoC4ToB5View inputView;
    private GamePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_c4_to_b5);

        inputView = (PianoC4ToB5View) findViewById(R.id.piano_view);
        GameMvp.View outputView = (SimpleNotesOutputView) findViewById(R.id.simple_notes_output_view);
        GameMvp.Model gameModel = new GameModel(new SongSequenceFactory(), new SimplePitchNotationFormatter());
        presenter = new GamePresenter(gameModel, outputView);
        presenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputView.attach(new NotesPlayedDispatcher(presenter));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.piano_c4_to_b5_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.restart_game) {
            presenter.onRestartGameSelected();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        inputView.detachKeyListener();
        super.onPause();
    }

    private static class NotesPlayedDispatcher implements KeyListener {

        private final Set<Note> notesOn = new HashSet<>();
        private final GamePresenter presenter;

        NotesPlayedDispatcher(GamePresenter presenter) {
            this.presenter = presenter;
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
            presenter.onNotesPlayed(notesOn.toArray(new Note[notesOn.size()]));
        }

    }
}
