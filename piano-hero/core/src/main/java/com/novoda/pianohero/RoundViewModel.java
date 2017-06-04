package com.novoda.pianohero;

class RoundViewModel {

    private final Sequence sequence;
    private final String successMessage;
    private final double successSound;
    private final String errorMessage;
    private final double errorSound;

    RoundViewModel(Sequence sequence,
                   String successMessage, double successSound,
                   String errorMessage, double errorSound) {
        this.sequence = sequence;
        this.successMessage = successMessage;
        this.successSound = successSound;
        this.errorMessage = errorMessage;
        this.errorSound = errorSound;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public double getSuccessSound() {
        return successSound;
    }

    public boolean hasError() {
        return sequence.hasError();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public double getErrorSound() {
        return errorSound;
    }
}
