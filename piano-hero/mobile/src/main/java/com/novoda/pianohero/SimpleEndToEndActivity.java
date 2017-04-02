package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SimpleEndToEndActivity extends AppCompatActivity {

    private SimpleNotesOutputView outputView;
    private EditText inputView;
    private Brain brain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_end_to_end);

        outputView = (SimpleNotesOutputView) findViewById(R.id.simple_notes_output_view);
        brain = new Brain(outputView);
        inputView = (EditText) findViewById(R.id.simple_notes_input_view);

        findViewById(R.id.simple_notes_button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputView.setVisibility(View.VISIBLE);
                inputView.setVisibility(View.VISIBLE);

                inputView.addTextChangedListener(new SimpleTextWatcher() {
                    @Override
                    protected void afterTextChanged(String text) {
                        try {
                            Note note = new SimpleNoteConverter().convert(text);
                            brain.onNotesPlayed(note);
                        } catch (IllegalArgumentException e) {
                            PianoHeroApplication.popToast("that's not a simple note!");
                        }

                        inputView.removeTextChangedListener(this);
                        inputView.setText(null);
                        inputView.addTextChangedListener(this);
                    }
                });

                ((Button) v).setText("Restart game");

                startNewGame();
            }
        });
    }

    private void startNewGame() {
        brain.start(new SongSequenceFactory().maryHadALittleLamb());
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
    protected void onPause() {
        brain.removeCallbacks();
        super.onPause();
    }
}
