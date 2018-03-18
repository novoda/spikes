package com.novoda.dungeoncrawler;

class Lava {
    public int left;
    public int right;
    public long lastOn;
    public long offtime;
    public long ontime;

    private State state;
    private boolean alive;

    void spawn(int left, int right, int ontime, int offtime, int offset, State state, long millis) {
        this.left = left;
        this.right = right;
        this.ontime = ontime;
        this.offtime = offtime;
        alive = true;
        lastOn = millis - offset;
        this.state = state;
    }

    void kill() {
        alive = false;
    }

    boolean isAlive() {
        return alive;
    }

    boolean isEnabled() {
        return state == Lava.State.ON;
    }

    void disable() {
        state = Lava.State.OFF;
    }

    void enable() {
        state = Lava.State.ON;
    }

    enum State {ON, OFF;}
}
