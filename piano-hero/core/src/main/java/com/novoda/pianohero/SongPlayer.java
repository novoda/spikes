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

    public void onStartPlayed(Note note) {
        callback.onRoundStart(note);
    }

    public void onStopPlaying(Note note) {
        playGameRound(note);
    }

    private void playGameRound(Note note) {
        if (sequence.isFinal(note)) {
            callback.onSongComplete();
            return;
        }

        if (sequence.currentNoteIs(note)) {
            this.sequence = sequence.nextPosition();
            callback.onRoundEnd(sequence);
            callback.onRoundSuccess(sequence);
        } else {
            Sequence updatedSequence = sequence.error();
            callback.onRoundEnd(updatedSequence);
            callback.onRoundError(updatedSequence);
        }
    }

    interface SongGameCallback {
        void onSongStarted(Sequence sequence);

        void onRoundStart(Note note);

        void onRoundEnd(Sequence sequence);

        void onRoundSuccess(Sequence sequence);

        void onRoundError(Sequence sequence);

        void onSongComplete();
    }
}
