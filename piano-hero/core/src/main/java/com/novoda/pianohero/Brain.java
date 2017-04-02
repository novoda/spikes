package com.novoda.pianohero;

public class Brain {

    private static final Callback NOOP_CALLBACK = new Callback() {
        @Override
        public void onSequenceComplete() {
            // no op
        }
    };

    private final SequenceDisplayer sequenceDisplayer;
    private Callback callback = NOOP_CALLBACK;

    private Sequence sequence;

    Brain(SequenceDisplayer sequenceDisplayer) {
        this.sequenceDisplayer = sequenceDisplayer;
    }

    public void attach(Callback callback) {
        this.callback = callback;
    }

    public void removeCallbacks() {
        this.callback = NOOP_CALLBACK;
    }

    public void start(Sequence sequence) {
        this.sequence = sequence;
        sequenceDisplayer.display(sequence);
    }

    public void onNotesPlayed(Note... notes) {
        onNotesPlayed(new Notes(notes));
    }

    public void onNotesPlayed(Notes notes) {
        if (notes.count() == 0) {
            return;
        }

        int position = sequence.position();
        Notes expectedNotes = sequence.get(position);
        if (notes.equals(expectedNotes)) {
            moveToNextPositionOrComplete();
        } else {
            displayIncorrect(notes);
        }
    }

    private void displayIncorrect(Notes notes) {
        Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(notes).build();
        sequenceDisplayer.display(updatedSequence);
    }

    private void moveToNextPositionOrComplete() {
        int position = sequence.position();
        if (position == sequence.length() - 1) {
            callback.onSequenceComplete();
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).atPosition(position + 1).build();
            sequenceDisplayer.display(updatedSequence);

            this.sequence = updatedSequence;
        }
    }

    public interface Callback {

        void onSequenceComplete();
    }
}
