package com.novoda.pianohero;

class RoundEndViewModel {

    private final Sequence sequence;
    private final String currentNoteFormatted;
    private final String nextNoteFormatted;
    private final String successMessage;
    private final String errorMessage;
    private final boolean isSharpError;

    RoundEndViewModel(Sequence sequence,
                      String currentNoteFormatted, String nextNoteFormatted,
                      String successMessage, String errorMessage,
                      boolean isSharpError) {
        this.sequence = sequence;
        this.currentNoteFormatted = currentNoteFormatted;
        this.nextNoteFormatted = nextNoteFormatted;
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

    public String getCurrentNoteFormatted() {
        return currentNoteFormatted;
    }

    public String getNextNoteFormatted() {
        return nextNoteFormatted;
    }
}
