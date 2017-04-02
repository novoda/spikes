package com.novoda.pianohero;

public class Brain {

    private static final Callback NOOP_CALLBACK = new Callback() {
        @Override
        public void onSequenceComplete() {
            // no op
        }
    };

    private final OnSequenceUpdatedCallback onSequenceUpdatedCallback;
    private Callback callback = NOOP_CALLBACK;

    private Sequence sequence;

    Brain(OnSequenceUpdatedCallback onSequenceUpdatedCallback) {
        this.onSequenceUpdatedCallback = onSequenceUpdatedCallback;
    }

    public void attach(Callback callback) {
        this.callback = callback;
    }

    public void removeCallbacks() {
        this.callback = NOOP_CALLBACK;
    }

    public void start(Sequence sequence) {
        this.sequence = sequence;
        onSequenceUpdatedCallback.onNext(sequence);
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
        onSequenceUpdatedCallback.onNext(updatedSequence);
    }

    private void moveToNextPositionOrComplete() {
        int position = sequence.position();
        if (position == sequence.length() - 1) {
            callback.onSequenceComplete();
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).atPosition(position + 1).build();
            onSequenceUpdatedCallback.onNext(updatedSequence);

            this.sequence = updatedSequence;
        }
    }

    public interface Callback {

        void onSequenceComplete();
    }
}
