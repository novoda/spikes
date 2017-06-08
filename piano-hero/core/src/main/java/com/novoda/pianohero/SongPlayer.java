package com.novoda.pianohero;

public class SongPlayer {

    private SongGameCallback callback;
    private Sequence sequence;

    public void attachListeners(SongGameCallback callback) {
        this.callback = callback;
    }

    public void loadSong(Sequence sequence) {
        this.sequence = sequence;
        callback.onSongStarted(sequence, currentNote(), nextNote());
    }

    public void onStartPlayed(Note note) {
        callback.onRoundStart(note);
    }

    public void onStopPlaying(Note note) {
        playGameRound(note);
    }

    private void playGameRound(Note note) {
        int currentPosition = sequence.position();
        Note expectedNote = sequence.get(currentPosition);
        if (currentPosition == sequence.length() - 1 && note.equals(expectedNote)) {
            callback.onSongComplete();
            return;
        }
        callback.onRoundEnd(sequence, currentNote(), nextNote());

        if (note.equals(expectedNote)) {
            this.sequence = new Sequence.Builder(sequence).withLatestError(null).atPosition(currentPosition + 1).build();
            callback.onRoundSuccess(sequence);
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(note).build();
            callback.onRoundError(updatedSequence);
        }
    }

    private Note currentNote() {
        return sequence.get(sequence.position());
    }

    private Note nextNote() {
        return sequence.get(sequence.position() + 1);
    }

    interface SongGameCallback {
        void onSongStarted(Sequence sequence, Note currentNote, Note nextNote);

        void onRoundStart(Note note);

        void onRoundEnd(Sequence sequence, Note currentNote, Note nextNote);

        void onRoundSuccess(Sequence sequence);

        void onRoundError(Sequence sequence);

        void onSongComplete();
    }
}
