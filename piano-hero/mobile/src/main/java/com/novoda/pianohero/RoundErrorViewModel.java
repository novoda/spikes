package com.novoda.pianohero;

class RoundErrorViewModel {

    private final Sequence sequence;
    private final String errorMessage;
    private final boolean isSharpError;
    private final int score;

    RoundErrorViewModel(Sequence sequence,
                        String errorMessage,
                        boolean isSharpError,
                        int score) {
        this.sequence = sequence;
        this.errorMessage = errorMessage;
        this.isSharpError = isSharpError;
        this.score = score;
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

    public String getScoreFormatted() {
        return "Score: " + score + " :-(";
    }
}
