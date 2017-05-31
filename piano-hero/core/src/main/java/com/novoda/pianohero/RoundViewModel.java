package com.novoda.pianohero;

class RoundViewModel {

    private final Sequence sequence;
    private final String statusMessage;

    RoundViewModel(Sequence sequence) {
        this(sequence, "");
    }

    RoundViewModel(Sequence sequence, String statusMessage) {
        this.sequence = sequence;
        this.statusMessage = statusMessage;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

}
