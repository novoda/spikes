package com.novoda.tpbot;

public enum Direction {
    FORWARD("↑", "w"),
    BACKWARD("↓", "s"),
    STEER_RIGHT("→", "d"),
    STEER_LEFT("←", "a");

    private final String representation;
    private final String rawCommand;

    Direction(String representation, String rawCommand) {
        this.representation = representation;
        this.rawCommand = rawCommand;
    }

    public String visualRepresentation() {
        return representation;
    }

    public String rawCommand() {
        return rawCommand;
    }
}
