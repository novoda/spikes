package com.novoda.tpbot;

public enum Direction {
    FORWARD("↑"),
    BACKWARD("↓"),
    STEER_RIGHT("→"),
    STEER_LEFT("←");

    private final String rawDirection;

    Direction(String rawDirection) {
        this.rawDirection = rawDirection;
    }

    public String visualRepresentation() {
        return rawDirection;
    }

    public static Direction from(String rawDirection) {
        for (Direction direction : values()) {
            if (direction.rawDirection.equalsIgnoreCase(rawDirection)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("No matching direction for: " + rawDirection);
    }
}
