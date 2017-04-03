package com.novoda.pianohero;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class SimpleEndToEndActivity extends AppCompatActivity {

    private AndroidKeyCodeToSimpleNoteConverter keyCodeConverter;
    private GameMvp.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_end_to_end);

        keyCodeConverter = new AndroidKeyCodeToSimpleNoteConverter();
        GameMvp.View outputView = (SimpleNotesOutputView) findViewById(R.id.simple_notes_output_view);
        GameMvp.Model gameModel = new GameModel(new SongSequenceFactory(), new SimplePitchNotationFormatter());
        presenter = new GamePresenter(gameModel, outputView);
        presenter.onCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_end_to_end_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_keyboard) {
            showKeyboard();
            return true;
        } else if (item.getItemId() == R.id.restart_game) {
            presenter.onRestartGameSelected();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        try {
            Note note = keyCodeConverter.convert(keyCode);
            presenter.onNotesPlayed(note);
        } catch (IllegalArgumentException e) {
            Log.e("!!!", "Non supported key input");
        }
        return true;
    }
}
