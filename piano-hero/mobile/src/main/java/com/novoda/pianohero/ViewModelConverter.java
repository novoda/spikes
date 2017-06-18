package com.novoda.pianohero;

import java.util.Locale;

class ViewModelConverter {

    private static final String NEXT_NOTE_HINT_FORMAT = "Next: %s";
    private static final String GAME_OVER_MESSAGE_FORMAT = "Well done! You scored %d";

    private final SimplePitchNotationFormatter pitchNotationFormatter;

    ViewModelConverter(SimplePitchNotationFormatter pitchNotationFormatter) {
        this.pitchNotationFormatter = pitchNotationFormatter;
    }

    GameOverViewModel createGameOverViewModel(State state) {
        return new GameOverViewModel(String.format(Locale.US, GAME_OVER_MESSAGE_FORMAT, state.getScore().points()));
    }

    GameInProgressViewModel createGameInProgressViewModel(State state) {
        Sequence sequence = state.getSequence();
        return new GameInProgressViewModel(
                state.getSound(),
                sequence,
                state.getMessage().getValue(),
                currentNoteFormatted(sequence.getCurrentNote()),
                nextNoteFormatted(sequence.getNextNote()),
                String.valueOf(state.getScore().points()),
                secondsRemainingFormatted(state.getSecondsRemaining())
        );
    }

    private String currentNoteFormatted(Note note) {
        return pitchNotationFormatter.format(note);
    }

    private String nextNoteFormatted(Note note) {
        String nextNote = pitchNotationFormatter.format(note);
        return String.format(Locale.US, NEXT_NOTE_HINT_FORMAT, nextNote);
    }

    private CharSequence secondsRemainingFormatted(long secondsRemaining) {
        return secondsRemaining + "s";
    }

}
