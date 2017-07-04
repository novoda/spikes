package com.novoda.pianohero;

public class RoundStartViewModel {
    private final double frequency;

    public RoundStartViewModel(double frequency) {
        this.frequency = frequency;
    }

    public double getNoteFrequency() {
        return frequency;
    }
}
