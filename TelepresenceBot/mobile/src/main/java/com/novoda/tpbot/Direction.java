package com.novoda.tpbot;

public enum Direction {
    FORWARD("↑"),
    BACKWARD("↓"),
    STEER_RIGHT("→"),
    STEER_LEFT("←");

    private final String representation;

    Direction(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }
}
