package com.novoda.pianohero;

import java.util.concurrent.TimeUnit;

class ViewModelConverter {

    private final PhrasesIterator phrasesIterator;
    private final long gameLengthMillis;

    ViewModelConverter(PhrasesIterator phrasesIterator, long gameLengthMillis) {
        this.phrasesIterator = phrasesIterator;
        this.gameLengthMillis = gameLengthMillis;
    }

    GameOverViewModel createGameOverViewModel(State state) {
        CharSequence gameOverMessage = phrasesIterator.nextGameOverMessage(state.getScore().points());
        return new GameOverViewModel(gameOverMessage);
    }

    GameInProgressViewModel createGameInProgressViewModel(State state) {
        Sequence sequence = state.getSequence();
        return new GameInProgressViewModel(
                state.getSound(),
                sequence,
                state.getMessage().getValue(),
                String.valueOf(state.getScore().points()),
                createTimeRemainingViewModel(state.getMillisRemaining())
        );
    }

    private TimeRemainingViewModel createTimeRemainingViewModel(long millisRemaining) {
        float progress = 1f * millisRemaining / gameLengthMillis;
        CharSequence remainingText = secondsRemainingFormatted(millisRemaining);
        return new TimeRemainingViewModel(progress, remainingText);
    }

    private CharSequence secondsRemainingFormatted(long millisRemaining) {
        return TimeUnit.MILLISECONDS.toSeconds(millisRemaining) + "s";
    }
}
