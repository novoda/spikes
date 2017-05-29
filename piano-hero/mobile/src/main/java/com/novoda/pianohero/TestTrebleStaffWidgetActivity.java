package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestTrebleStaffWidgetActivity extends AppCompatActivity {

    private Sequence sequence;
    private PojoViewWrapper pojoViewWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_treble_staff_widget);

        pojoViewWrapper = new PojoViewWrapper(
                (TrebleStaffWidget) findViewById(R.id.treble_staff_widget),
                (TextView) findViewById(R.id.encouraging_text_view),
                new SimplePitchNotationFormatter()
        );
        reset(null);
    }

    public void playCorrectNote(View view) {
        sequence = new Sequence.Builder(sequence)
                .atPosition(sequence.position() + 1)
                .withLatestError(Notes.EMPTY)
                .build();
        pojoViewWrapper.show(sequence);
    }

    public void playIncorrectNote(View view) {
        sequence = new Sequence.Builder(sequence)
                .withLatestError(new Notes(new Note(63)))
                .build();
        pojoViewWrapper.show(sequence);
    }

    public void reset(View view) {
        sequence = new SongSequenceFactory().maryHadALittleLamb();
        pojoViewWrapper.show(sequence);
    }

    private static class PojoViewWrapper {

        private final TrebleStaffWidget trebleStaffWidget;
        private final TextView encouragingTextView;
        private final SimplePitchNotationFormatter formatter;

        PojoViewWrapper(TrebleStaffWidget trebleStaffWidget, TextView encouragingTextView, SimplePitchNotationFormatter formatter) {
            this.trebleStaffWidget = trebleStaffWidget;
            this.encouragingTextView = encouragingTextView;
            this.formatter = formatter;
        }

        void show(Sequence sequence) {
            List<Note> notes = notesListToNoteList(sequence.notes());
            if (sequence.latestError().count() == 0) {
                trebleStaffWidget.show(notes, sequence.position());
                encouragingTextView.setText(sequence.position() == 0 ? "Let's go!" : "Woohoo, keep going!");
            } else {
                Note errorNote = firstNoteFrom(sequence.latestError());
                trebleStaffWidget.show(notes, sequence.position(), errorNote);
                encouragingTextView.setText(String.format(Locale.US, "Uh-oh, that was %s! The correct note is %s", formatter.format(errorNote), formatter.format(notes.get(sequence.position()))));
            }
        }

        // all this is because we have chords atm but the TrebleStaffWidget doesn't support chords
        private static List<Note> notesListToNoteList(List<Notes> notesList) {
            ArrayList<Note> noteList = new ArrayList<>(notesList.size());
            for (Notes notes : notesList) {
                noteList.add(firstNoteFrom(notes));
            }
            return noteList;
        }

        private static Note firstNoteFrom(Notes notes) {
            for (Note note : notes) {
                return note;
            }
            throw new IllegalArgumentException("notes was empty? you not cool.");
        }

    }

}
