package com.novoda.pianohero;

public class SongPlayer {

    private SongGameCallback callback;
    private Sequence sequence;

    public void attachListeners(SongGameCallback callback) {
        this.callback = callback;
    }

    public void loadSong(Sequence sequence) {
        this.sequence = sequence;
    }

    public void onStartPlaying(Note note) {
        callback.onStartPlayingNote(note, sequence);
    }

    public void onStopPlaying(Note note) {
        if (sequence.isFinal(note)) {
            callback.onFinalNoteInSequencePlayedSuccessfully();
            return;
        }

        if (sequence.currentNoteIs(note)) {
            this.sequence = sequence.nextPosition();
            callback.onCorrectNotePlayed(sequence);
        } else {
            Sequence updatedSequence = sequence.error(note);
            callback.onIncorrectNotePlayed(updatedSequence);
        }
    }

    public Sequence getSequence() {
        return sequence;
    }

    interface SongGameCallback {

        void onStartPlayingNote(Note note, Sequence sequence);

        void onCorrectNotePlayed(Sequence sequence);

        void onIncorrectNotePlayed(Sequence sequence);

        void onFinalNoteInSequencePlayedSuccessfully();
    }
}
