package com.novoda.tpbot.model;

public enum Direction {
    FORWARD("↑", "w"),
    BACKWARD("↓", "s"),
    STEER_RIGHT("→", "d"),
    STEER_LEFT("←", "a");

    private final String visualRepresentation;
    private final String rawDirection;

    Direction(String visualRepresentation, String rawDirection) {
        this.visualRepresentation = visualRepresentation;
        this.rawDirection = rawDirection;
    }

    public String visualRepresentation() {
        return visualRepresentation;
    }

    public static Direction from(String rawDirection) {
        for (Direction direction : values()) {
            if (direction.rawDirection.equalsIgnoreCase(rawDirection)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("No matching direction for: " + rawDirection);
    }

    public String rawDirection() {
        return rawDirection;
    }
}
