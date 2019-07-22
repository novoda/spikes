package com.novoda.dungeoncrawler;

class PausableStartClock implements StartClock {

    private final DefaultStartClock startClock;
    private long currentPauseStart = 0;
    private long pausedTimeAccumulator = 0;

    PausableStartClock(DefaultStartClock startClock) {
        this.startClock = startClock;
    }

    @Override
    public long millis() {
        if (currentPauseStart != 0) {
            return currentPauseStart;
        }
        return startClock.millis() - pausedTimeAccumulator;
    }

    void pause() {
        if (currentPauseStart != 0) {
            return;
        }
        currentPauseStart = startClock.millis();
        pausedTimeAccumulator = 0;
    }

    void resume() {
        if (currentPauseStart == 0) {
            return;
        }

        long pauseDuration = startClock.millis() - currentPauseStart;
        pausedTimeAccumulator += pauseDuration;
        currentPauseStart = 0;
    }

}
