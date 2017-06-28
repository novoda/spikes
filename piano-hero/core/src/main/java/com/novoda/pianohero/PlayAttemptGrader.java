package com.novoda.pianohero;

class PlayAttemptGrader {

    void grade(Note note, Sequence sequence, Callback callback) {
        if (sequence.isFinal(note)) {
            callback.onFinalNoteInSequencePlayedSuccessfully();
            return;
        }

        if (sequence.currentNoteIs(note)) {
            callback.onCorrectNotePlayed(sequence.nextPosition());
        } else {
            callback.onIncorrectNotePlayed(sequence.error(note));
        }
    }

    interface Callback {

        void onCorrectNotePlayed(Sequence sequence);

        void onIncorrectNotePlayed(Sequence sequence);

        void onFinalNoteInSequencePlayedSuccessfully();
    }
}
