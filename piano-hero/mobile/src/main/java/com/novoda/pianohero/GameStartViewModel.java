package com.novoda.pianohero;

class GameStartViewModel {

    private final Sequence sequence;
    private final String currentNoteFormatted;
    private final String nextNoteFormatted;
    private final String startMessage;

    GameStartViewModel(Sequence sequence,
                       String currentNoteFormatted, String nextNoteFormatted,
                       String startMessage) {
        this.sequence = sequence;
        this.currentNoteFormatted = currentNoteFormatted;
        this.nextNoteFormatted = nextNoteFormatted;
        this.startMessage = startMessage;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public String getCurrentNoteFormatted() {
        return currentNoteFormatted;
    }

    public String getNextNoteFormatted() {
        return nextNoteFormatted;
    }
}
