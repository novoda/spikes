package com.novoda.tpbot;

public enum Direction {
    FORWARD("↑", "w"),
    BACKWARD("↓", "s"),
    STEER_RIGHT("→", "d"),
    STEER_LEFT("←", "a");

    private final String representation;
    private final String rawDirection;

    Direction(String representation, String rawDirection) {
        this.representation = representation;
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

    public String rawDirection() {
        return rawDirection;
    }
}
