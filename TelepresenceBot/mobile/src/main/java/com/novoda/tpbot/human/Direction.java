package com.novoda.tpbot.human;

enum Direction {
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
