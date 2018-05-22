package com.novoda.dungeoncrawler;

import static com.novoda.dungeoncrawler.Direction.RIGHT_TO_LEFT;

class Conveyor {
    private boolean alive;
    public Direction direction;
    private int speed;
    public int startPoint;
    public int endPoint;

    void spawn(int startPoint, int endPoint, Direction direction, int speed) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = direction;
        this.speed = speed;
        this.alive = true;
    }

    void kill() {
        alive = false;
    }

    boolean isAlive() {
        return alive;
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
}
