package com.novoda.pianohero;

import java.util.Locale;

public class GameModel implements GameMvp.Model {

    private final SongSequenceFactory songSequenceFactory;
    private final SimplePitchNotationFormatter pitchNotationFormatter;
    private final Piano piano;

    private Sequence sequence;

    GameModel(
            SongSequenceFactory songSequenceFactory,
            SimplePitchNotationFormatter pitchNotationFormatter,
            Piano piano) {
        this.songSequenceFactory = songSequenceFactory;
        this.pitchNotationFormatter = pitchNotationFormatter;
        this.piano = piano;
    }

    @Override
    public void startGame(StartCallback callback,
                          final RoundCallback roundCallback,
                          final CompletionCallback completionCallback) {
        piano.attachListener(new Piano.NoteListener() {
            @Override
            public void onStart(Note note) {
                roundCallback.onRoundStart(440 * (2 ^ ((note.midi() - 69) / 12)));
            }

            @Override
            public void onStop(Note note) {
                playGameRound(roundCallback, completionCallback, note);
            }
        });

        sequence = songSequenceFactory.maryHadALittleLamb();
        callback.onGameStarted(createViewModel(sequence));
    }

    @Override
    public void startup() {
        piano.open();
    }

    private RoundViewModel createViewModel(Sequence sequence) {
        String successMessage = getSuccessMessage(sequence);
        double successSound = getSuccessSound(sequence);
        String errorMessage = getErrorMessage(sequence);
        double errorSound = getSuccessSound(sequence);
        return new RoundViewModel(sequence, successMessage, successSound, errorMessage, errorSound);
    }

    private String getSuccessMessage(Sequence sequence) {
        if (sequence.position() > 0) {
            return String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length());
        } else {
            return "";
        }
    }

    private double getSuccessSound(Sequence sequence) {
        Note note = sequence.get(sequence.position());
        return 440 * (2 ^ ((note.midi() - 69) / 12));
    }

    private String getErrorMessage(Sequence sequence) {
        if (sequence.hasError()) {
            String nextNoteAsText = pitchNotationFormatter.format(sequence.get(sequence.position()));
            String latestErrorAsText = pitchNotationFormatter.format(sequence.latestError());
            return String.format(Locale.US, "Ruhroh! The correct note is %s but you played %s.", nextNoteAsText, latestErrorAsText);
        } else {
            return "";
        }
    }

    private double getErrorSound() {
        // Could make a twanging noise if you wanted to get serious
        Note note = sequence.get(sequence.position());
        return 440 * (2 ^ ((note.midi() - 69) / 12));
    }

    @Override
    public void playGameRound(
            RoundCallback roundCallback,
            CompletionCallback completionCallback,
            Note note
    ) {
        int currentPosition = sequence.position();
        Note expectedNote = sequence.get(currentPosition);
        if (currentPosition == sequence.length() - 1 && note.equals(expectedNote)) {
            completionCallback.onGameComplete();
            return;
        }

        if (note.equals(expectedNote)) {
            this.sequence = new Sequence.Builder(sequence).withLatestError(null).atPosition(currentPosition + 1).build();
            roundCallback.onRoundUpdate(createViewModel(sequence));
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(note).build();
            roundCallback.onRoundUpdate(createViewModel(updatedSequence));
        }
    }

    @Override
    public void shutdown() {
        piano.close();
    }

}
