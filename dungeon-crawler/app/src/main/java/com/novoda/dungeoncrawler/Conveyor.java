package com.novoda.dungeoncrawler;

public class Conveyor {
    private boolean alive;
    public int direction;
    public int startPoint;
    public int endPoint;

    public void spawn(int startPoint, int endPoint, int direction) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = direction;
        this.alive = true;
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }
}
