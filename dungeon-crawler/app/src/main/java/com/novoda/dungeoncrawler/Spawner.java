package com.novoda.dungeoncrawler;

public class Spawner {
    public long activate;
    public long lastSpawned;
    public long rate;
    public int position;
    public int direction;
    public int sp;
    private boolean alive;

    public void spawn(int position, int rate, int sp, int direction, int activate, long millis) {
        this.position = position;
        this.rate = rate;
        this.sp = sp;
        this.direction = direction;
        this.activate = millis + activate;
        this.alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
        this.lastSpawned = 0;
    }
}
