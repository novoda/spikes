package com.novoda.pianohero;

import java.util.Locale;

class ViewModelConverter {

    private static final String NEXT_NOTE_HINT_FORMAT = "Next: %s";

    private final SimplePitchNotationFormatter pitchNotationFormatter;

    ViewModelConverter(SimplePitchNotationFormatter pitchNotationFormatter) {
        this.pitchNotationFormatter = pitchNotationFormatter;
    }

    private double frequencyFor(Note note) {
        return 440 * Math.pow(2, (note.midi() - 69) * 1f / 12);
    }

    ClockViewModel createClockViewModel(long secondsLeft) {
        return new ClockViewModel(secondsLeft + "s");
    }

    private String currentNoteFormatted(Note note) {
        return pitchNotationFormatter.format(note);
    }

    private String nextNoteFormatted(Note note) {
        String nextNote = pitchNotationFormatter.format(note);
        return String.format(Locale.US, NEXT_NOTE_HINT_FORMAT, nextNote);
    }

    private String getSuccessMessage(Sequence sequence) {
        if (sequence.position() > 0) {
            return String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length());
        } else {
            return "";
        }
    }

    private String getErrorMessage(Sequence sequence) {
        String currentNoteAsText = currentNoteFormatted(sequence.getNextNote());
        String latestErrorAsText = pitchNotationFormatter.format(sequence.latestError());
        return String.format(Locale.US, "Ruhroh! The correct note is %s but you played %s.", currentNoteAsText, latestErrorAsText);
    }

    GameOverViewModel createGameOverViewModel(Score score) {
        return new GameOverViewModel(String.format(Locale.US, "Well done! You scored %d", score.points()));
    }

    GameInProgressViewModel createStartGameInProgressViewModel(Sequence sequence, Score score) {
        String message = "Let's start!";

        return new GameInProgressViewModel(
                Sound.ofSilence(),
                sequence,
                message,
                currentNoteFormatted(sequence.getCurrentNote()),
                nextNoteFormatted(sequence.getCurrentNote()),
                String.valueOf(score.points())
        );
    }

    public GameInProgressViewModel createCurrentlyPressingNoteGameInProgressViewModel(Note note, Sequence sequence, Score score) {
        return new GameInProgressViewModel(
                Sound.atFrequency(frequencyFor(note)),
                sequence,
                "",
                currentNoteFormatted(sequence.getCurrentNote()),
                nextNoteFormatted(sequence.getNextNote()),
                String.valueOf(score.points())
        );
    }

    public GameInProgressViewModel createCorrectNotePressedGameInProgressViewModel(Sequence sequence, Score score) {
        return new GameInProgressViewModel(
                Sound.ofSilence(),
                sequence,
                getSuccessMessage(sequence),
                currentNoteFormatted(sequence.getCurrentNote()),
                nextNoteFormatted(sequence.getNextNote()),
                String.valueOf(score.points())
        );
    }

    public GameInProgressViewModel createIncorrectNotePressedGameInProgressViewModel(Sequence sequence, Score score) {
        return new GameInProgressViewModel(
                Sound.ofSilence(),
                sequence,
                getErrorMessage(sequence),
                currentNoteFormatted(sequence.getCurrentNote()),
                nextNoteFormatted(sequence.getNextNote()),
                String.valueOf(score.points())
        );
    }
}
