package com.novoda.pianohero;

class RoundErrorViewModel {

    private final Sequence sequence;
    private final String errorMessage;
    private final boolean isSharpError;

    RoundErrorViewModel(Sequence sequence,
                        String errorMessage,
                        boolean isSharpError) {
        this.sequence = sequence;
        this.errorMessage = errorMessage;
        this.isSharpError = isSharpError;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSharpError() {
        return isSharpError;
    }

}
