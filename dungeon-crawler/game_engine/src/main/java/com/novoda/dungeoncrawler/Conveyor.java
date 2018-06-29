package com.novoda.dungeoncrawler;

import static com.novoda.dungeoncrawler.Direction.RIGHT_TO_LEFT;

public class Conveyor {
    private final Direction direction;
    private final int speed;
    private final int startPoint;
    private final int endPoint;

    Conveyor(int startPoint, int endPoint, Direction direction, int speed) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = direction;
        this.speed = speed;
    }

    public int affect(int position) {
        if (!contains(position)) {
            return 0;
        }
        if (direction == RIGHT_TO_LEFT) {
            return -speed;
        } else {
            return speed;
        }
    }

    private boolean contains(int point) {
        return startPoint < point && point < endPoint;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }
}
