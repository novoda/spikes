package com.novoda.dungeoncrawler;

public class Lava {
    public State state;
    public int left;
    public int right;
    public long lastOn;
    public long offtime;
    public long ontime;
    private boolean alive;

    public void spawn(int left, int right, int ontime, int offtime, int offset, State state, long millis) {
        this.left = left;
        this.right = right;
        this.ontime = ontime;
        this.offtime = offtime;
        alive = true;
        lastOn = millis - offset;
        this.state = state;
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    enum State {ON, OFF;}
}
