package com.novoda.pianohero;

class RoundEndViewModel {

    private final Sequence sequence;
    private final String successMessage;
    private final String errorMessage;
    private final boolean isSharpError;

    RoundEndViewModel(Sequence sequence,
                      String successMessage,
                      String errorMessage,
                      boolean isSharpError) {
        this.sequence = sequence;
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;
        this.isSharpError = isSharpError;
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

    public boolean isSharpError() {
        return isSharpError;
    }
}
