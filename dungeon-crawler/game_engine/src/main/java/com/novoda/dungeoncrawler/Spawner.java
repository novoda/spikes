package com.novoda.dungeoncrawler;

class Spawner {
    public long activate;
    public long lastSpawned;
    public long rate;
    public int position;
    public int direction;
    public int speed;
    private boolean alive;

    void spawn(int position, int rate, int speed, int direction, int activate, long millis) {
        this.position = position;
        this.rate = rate;
        this.speed = speed;
        this.direction = direction;
        this.activate = millis + activate;
        this.alive = true;
    }

    boolean isAlive() {
        return alive;
    }

    void kill() {
        this.alive = false;
        this.lastSpawned = 0;
    }
}
