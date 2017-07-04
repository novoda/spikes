package com.novoda.pianohero;

final class Score {

    private static final int INITIAL_VALUE = 500;
    private static final int INCREMENT_VALUE = 7;
    private static final int DECREMENT_VALUE = -3;

    private final int points;

    static Score initial() {
        return new Score(INITIAL_VALUE);
    }

    private Score(int points) {
        this.points = points;
    }

    Score increment() {
        return new Score(this.points + INCREMENT_VALUE);
    }

    Score decrement() {
        return new Score(this.points + DECREMENT_VALUE);
    }

    int points() {
        return points;
    }

}
