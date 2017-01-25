package com.novoda.tpbot.support;

public enum Event {

    JOIN_HUMAN("join_as_human"),
    JOIN_BOT("join_as_bot"),
    MOVE_IN("move_in");

    private final String rawEvent;

    Event(String rawEvent) {
        this.rawEvent = rawEvent;
    }

    public String rawEvent() {
        return rawEvent;
    }

}
