package com.novoda.pianohero;

import android.support.annotation.Nullable;

class Sound {
    @Nullable
    private final Double frequency;

    public static Sound ofSilence() {
        return new Sound(null);
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
