package com.novoda.pianohero;

public class GameInProgressViewModel {

    private final Sound sound;
    private final Sequence sequence;
    private final CharSequence message;
    private final CharSequence score;
    private final TimeRemainingViewModel timeRemaining;

    public GameInProgressViewModel(Sound sound, Sequence sequence, CharSequence message, CharSequence score, TimeRemainingViewModel timeRemaining) {
        this.sound = sound;
        this.sequence = sequence;
        this.message = message;
        this.score = score;
        this.timeRemaining = timeRemaining;
    }

    public Sound getSound() {
        return sound;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public CharSequence getMessage() {
        return message;
    }

    public CharSequence getScore() {
        return score;
    }

    public TimeRemainingViewModel getTimeRemaining() {
        return timeRemaining;
    }
}
