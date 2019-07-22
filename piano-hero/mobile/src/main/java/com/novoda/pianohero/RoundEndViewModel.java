package com.novoda.pianohero;

class RoundEndViewModel {

    private final Sequence sequence;
    private final String currentNoteFormatted;
    private final String nextNoteFormatted;
    private final String successMessage;

    RoundEndViewModel(Sequence sequence,
                      String currentNoteFormatted, String nextNoteFormatted,
                      String successMessage) {
        this.sequence = sequence;
        this.currentNoteFormatted = currentNoteFormatted;
        this.nextNoteFormatted = nextNoteFormatted;
        this.successMessage = successMessage;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public String getCurrentNoteFormatted() {
        return currentNoteFormatted;
    }

    public String getNextNoteFormatted() {
        return nextNoteFormatted;
    }
}
