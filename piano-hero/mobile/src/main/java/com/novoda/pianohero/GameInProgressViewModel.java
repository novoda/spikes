package com.novoda.pianohero;

public class GameInProgressViewModel {

    private final Sound sound;
    private final Sequence sequence;
    private final CharSequence message;
    private final CharSequence currentNote;
    private final CharSequence upcomingNote;
    private final CharSequence score;

    public GameInProgressViewModel(Sound sound, Sequence sequence, CharSequence message, CharSequence currentNote, CharSequence upcomingNote, CharSequence score) {
        this.sound = sound;
        this.sequence = sequence;
        this.message = message;
        this.currentNote = currentNote;
        this.upcomingNote = upcomingNote;
        this.score = score;
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

    public CharSequence getCurrentNote() {
        return currentNote;
    }

    public CharSequence getUpcomingNote() {
        return upcomingNote;
    }

    public CharSequence getScore() {
        return score;
    }
}
