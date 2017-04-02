package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
                            Notes notes = new Notes(note);
                            Log.d("!!!", "afterTextChanged: " + notes);
                            brain.onNotesPlayed(notes);
                        } catch (IllegalArgumentException e) {
                            PianoHeroApplication.popToast("that's not a simple note!");
                        }
                    }
                });

                ((Button) v).setText("Restart game");

                startNewGame();
            }
        });
    }

    private static class SimpleNoteConverter {

        private static final Map<String, Note> MAP = new HashMap<>();

        static {
            MAP.put("c", Note.C4);
            MAP.put("d", Note.D4);
            MAP.put("e", Note.E4);
            MAP.put("f", Note.F4);
            MAP.put("g", Note.G4);
            MAP.put("a", Note.A4);
            MAP.put("b", Note.B4);
        }

        public Note convert(String raw) {
            Note note = MAP.get(raw.toLowerCase(Locale.US));
            if (note == null) {
                throw new IllegalArgumentException("Argument doesn't correspond to simple note: " + raw);
            } else {
                return note;
            }
        }
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
