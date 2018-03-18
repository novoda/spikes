package com.novoda.dungeoncrawler;

class Conveyor {
    private boolean alive;
    public int direction;
    public int startPoint;
    public int endPoint;

    void spawn(int startPoint, int endPoint, int direction) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = direction;
        this.alive = true;
    }

    void kill() {
        alive = false;
    }

    boolean isAlive() {
        return alive;
    }
}
