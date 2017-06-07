package com.novoda.pianohero;

class GameStartViewModel {

    private final Sequence sequence;
    private final String startMessage;

    GameStartViewModel(Sequence sequence,
                       String startMessage) {
        this.sequence = sequence;
        this.startMessage = startMessage;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getStartMessage() {
        return startMessage;
    }

}
