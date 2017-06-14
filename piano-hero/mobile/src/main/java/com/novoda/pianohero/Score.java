package com.novoda.pianohero;

class Score {

    private final int points;

    Score(int points) {
        this.points = points;
    }

    Score add(int points) {
        return new Score(this.points + points);
    }

    Score minus(int points) {
        return add(-points);
    }

    int points() {
        return points;
    }

}
