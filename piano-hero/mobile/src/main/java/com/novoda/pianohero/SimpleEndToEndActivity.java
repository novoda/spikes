package com.novoda.pianohero;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class SimpleEndToEndActivity extends AppCompatActivity {

    private final AndroidKeyCodeToSimpleNoteConverter keyCodeConverter = new AndroidKeyCodeToSimpleNoteConverter();

    private Brain brain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_end_to_end);

        SimpleNotesOutputView outputView = (SimpleNotesOutputView) findViewById(R.id.simple_notes_output_view);
        brain = new Brain(outputView); // TODO: brain shouldn't touch view direct

        startNewGame();
    }

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
        });
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
            startNewGame();
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
            brain.onNotesPlayed(note);
        } catch (IllegalArgumentException e) {
            PianoHeroApplication.popToast("that's not a simple note!");
        }
        return true;
    }

    @Override
    protected void onPause() {
        brain.removeCallbacks();
        super.onPause();
    }
}
