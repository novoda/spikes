package com.novoda.pianohero;

import android.support.annotation.Nullable;

class Sound {
    @Nullable
    private final Double frequency;

    public static Sound ofSilence() {
        return new Sound(null);
    }

    public static Sound of(Note note) {
        return atFrequency(440 * Math.pow(2, (note.midi() - 69) * 1f / 12));
    }

    public static Sound atFrequency(double frequency) {
        return new Sound(frequency);
    }

    private Sound(@Nullable Double frequency) {
        this.frequency = frequency;
    }

    public boolean isOfSilence() {
        return frequency == null;
    }

    public double frequency() {
        if (isOfSilence()) {
            throw new RuntimeException("silence has no frequency. I think.");
        }
        return frequency;
    }
}
