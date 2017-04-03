package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

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
            presenter.onShowKeyboardSelected();
            return true;
        } else if (item.getItemId() == R.id.restart_game) {
            presenter.onRestartGameSelected();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
