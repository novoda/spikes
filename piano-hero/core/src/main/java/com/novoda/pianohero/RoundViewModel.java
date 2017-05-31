package com.novoda.pianohero;

class RoundViewModel {
    private final String note;
    private final String statusMessage;

    RoundViewModel(String note, String statusMessage) {
        this.note = note;
        this.statusMessage = statusMessage;
    }

    public String getNote() {
        return note;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

}
