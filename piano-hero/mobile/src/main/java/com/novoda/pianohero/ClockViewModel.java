package com.novoda.pianohero;

public class ClockViewModel {

    private final String message;

    public ClockViewModel(String message) {
        this.message = message;
    }

    public String getTimeLeftFormatted() {
        return message;
    }
}
