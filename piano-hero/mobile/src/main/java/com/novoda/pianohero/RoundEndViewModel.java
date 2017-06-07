package com.novoda.pianohero;

class RoundEndViewModel {

    private final Sequence sequence;
    private final String successMessage;
    private final String errorMessage;

    RoundEndViewModel(Sequence sequence,
                      String successMessage,
                      String errorMessage) {
        this.sequence = sequence;
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public boolean hasError() {
        return sequence.hasError();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
