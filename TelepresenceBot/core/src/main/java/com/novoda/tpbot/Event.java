package com.novoda.tpbot;

public enum Event {

    MOVE_IN("move_in"),
    DIRECTION("direction");

    private final String rawEvent;

    Event(String rawEvent) {
        this.rawEvent = rawEvent;
    }

    public String rawEvent() {
        return rawEvent;
    }

}
