package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestTrebleStaffWidgetActivity extends AppCompatActivity {

    private Sequence sequence;
    private ThingyMvpView thingyMvpView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_treble_staff_widget);

        TrebleStaffWidget trebleStaffWidget = (TrebleStaffWidget) findViewById(R.id.treble_staff_widget);
        thingyMvpView = new ThingyMvpView(trebleStaffWidget);
        reset(null);
    }

    public void playCorrectNote(View view) {
        sequence = new Sequence.Builder(sequence)
                .atPosition(sequence.position() + 1)
                .withLatestError(Notes.EMPTY)
                .build();
        thingyMvpView.show(sequence);
    }

    public void playIncorrectNote(View view) {
        sequence = new Sequence.Builder(sequence)
                .withLatestError(new Notes(Note.B5))
                .build();
        thingyMvpView.show(sequence);
    }

    public void reset(View view) {
        sequence = new SongSequenceFactory().maryHadALittleLamb();
        thingyMvpView.show(sequence);
    }

    private static class ThingyMvpView {

        private final TrebleStaffWidget trebleStaffWidget;

        ThingyMvpView(TrebleStaffWidget trebleStaffWidget) {
            this.trebleStaffWidget = trebleStaffWidget;
        }

        public void show(Sequence sequence) {
            List<Note> notes = notesListToNoteList(sequence.notes());
            Notes error = sequence.latestError();
            if (error.count() == 0) {
                trebleStaffWidget.show(notes, sequence.position());
            } else {
                trebleStaffWidget.show(notes, sequence.position(), firstNoteFrom(error));
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
