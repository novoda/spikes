package com.novoda.pianohero;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

class ViewModelConverter {

    private static final String NEXT_NOTE_HINT_FORMAT = "Next: %s";
    private static final String GAME_OVER_MESSAGE_FORMAT = "Brilliant, you scored %d!";

    private final SimplePitchNotationFormatter pitchNotationFormatter;
    private final long gameLengthMillis;

    ViewModelConverter(SimplePitchNotationFormatter pitchNotationFormatter, long gameLengthMillis) {
        this.pitchNotationFormatter = pitchNotationFormatter;
        this.gameLengthMillis = gameLengthMillis;
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
                createTimeRemainingViewModel(state.getMillisRemaining())
        );
    }

    private TimeRemainingViewModel createTimeRemainingViewModel(long millisRemaining) {
        float progress = 1 - (1f * millisRemaining / gameLengthMillis);
        CharSequence remainingText = secondsRemainingFormatted(millisRemaining);
        return new TimeRemainingViewModel(progress, remainingText);
    }

    private String currentNoteFormatted(Note note) {
        return pitchNotationFormatter.format(note);
    }

    private String nextNoteFormatted(Note note) {
        String nextNote = pitchNotationFormatter.format(note);
        return String.format(Locale.US, NEXT_NOTE_HINT_FORMAT, nextNote);
    }

    private CharSequence secondsRemainingFormatted(long millisRemaining) {
        return TimeUnit.MILLISECONDS.toSeconds(millisRemaining) + "s";
    }
}
