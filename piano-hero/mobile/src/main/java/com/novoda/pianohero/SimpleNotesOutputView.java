package com.novoda.pianohero;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleNotesOutputView extends LinearLayout {

    private final SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();

    private TextView nextNoteToPlayTextView;
    private TextView messageTextView;

    public SimpleNotesOutputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_simple_notes_output, this);
        nextNoteToPlayTextView = (TextView) findViewById(R.id.simple_notes_output_text_next_note);
        messageTextView = (TextView) findViewById(R.id.simple_notes_output_text_message);
    }

    public void display(Sequence sequence) {
        checkSequenceIsSimpleElseThrow(sequence);

        updateNextNoteToPlay(sequence);
        updateMessage(sequence);
    }

    private void checkSequenceIsSimpleElseThrow(Sequence sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            Notes notes = sequence.get(i);
            if (notes.count() > 1) {
                throw new IllegalArgumentException("Sequence contains chords, that's not simple enough");
            }
            for (Note note : notes.notes()) {
                if (simplePitchNotationFormatter.format(note).endsWith("#")) {
                    throw new IllegalArgumentException("Sequence contains sharps, that's not simple enough");
                }
            }
        }
    }

    private void updateNextNoteToPlay(Sequence sequence) {
        int nextNotesPosition = sequence.position();
        for (Note note : sequence.get(nextNotesPosition)) {
            updateNextNoteToPlayTextViewWith(note);
        }
    }

    private void updateNextNoteToPlayTextViewWith(Note note) {
        String simpleNotation = simplePitchNotationFormatter.format(note);
        nextNoteToPlayTextView.setText(simpleNotation);
    }

    private void updateMessage(Sequence sequence) {
        if (sequence.latestError().count() == 0) {
            if (sequence.position() > 0) {
                updateMessageTextViewWith("Woo! Keep going!");
            } else {
                messageTextView.setVisibility(INVISIBLE);
            }
        } else {
            updateMessageTextViewWith("Ruhroh, try again!");
        }
    }

    private void updateMessageTextViewWith(String message) {
        messageTextView.setVisibility(VISIBLE);
        messageTextView.setText(message);
    }
}
