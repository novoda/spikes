package com.novoda.dungeoncrawler;

public class Lava {
    private final int left;
    private final int right;
    private final long offTime;
    private final long onTime;

    private long lastToggle;
    private State state;

    Lava(int left, int right, int onTime, int offTime, State state) {
        this.left = left;
        this.right = right;
        this.onTime = onTime;
        this.offTime = offTime;
        this.state = state;
    }

    boolean isEnabled() {
        return state == Lava.State.ON;
    }

    void toggleLava(long currentTime) {
        if (isEnabled()) {
            if (hasBeenOnLongEnough(currentTime)) {
                disable(currentTime);
            }
        } else {
            if (hasBeenOffLongEnough(currentTime)) {
                enable(currentTime);
            }
        }
    }

    private void disable(long disabledAtTime) {
        state = Lava.State.OFF;
        lastToggle = disabledAtTime;
    }

    private boolean hasBeenOnLongEnough(long currentTime) {
        return lastToggle + getOnTime() < currentTime;
    }

    private boolean hasBeenOffLongEnough(long currentTime) {
        return lastToggle + getOffTime() < currentTime;
    }

    private void enable(long enabledAtTime) {
        state = Lava.State.ON;
        lastToggle = enabledAtTime;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    private long getOffTime() {
        return offTime;
    }

    private long getOnTime() {
        return onTime;
    }

    public boolean consumes(int pos) {
        return isEnabled() && contains(pos);
    }

    private boolean contains(int position) {
        return left < position && right > position;
    }

    enum State {ON, OFF;}
}
