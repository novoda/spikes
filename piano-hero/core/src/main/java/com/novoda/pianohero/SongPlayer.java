package com.novoda.pianohero;

public class SongPlayer {

    private SongGameCallback callback;
    private Sequence sequence;

    public void attachListeners(SongGameCallback callback) {
        this.callback = callback;
    }

    public void loadSong(Sequence sequence) {
        this.sequence = sequence;
        callback.onSongStarted(sequence);
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

    interface SongGameCallback {
        void onSongStarted(Sequence sequence);

        void onStartPlayingNote(Note note, Sequence sequence);

        void onCorrectNotePlayed(Sequence sequence);

        void onIncorrectNotePlayed(Sequence sequence);

        void onFinalNoteInSequencePlayedSuccessfully();
    }
}
