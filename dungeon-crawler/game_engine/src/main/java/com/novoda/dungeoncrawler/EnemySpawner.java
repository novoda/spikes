package com.novoda.dungeoncrawler;

public class EnemySpawner {
    private final long activate;
    private final long rate;
    private final int position;
    private final int direction;
    private final int speed;

    private long lastSpawned;
    private boolean alive;

    EnemySpawner(int position, int rate, int speed, int direction, int activate, long millis) {
        this.position = position;
        this.rate = rate;
        this.speed = speed;
        this.direction = direction;
        this.activate = millis + activate;
        this.alive = true;
    }

    void kill() {
        this.alive = false;
        this.lastSpawned = 0;
    }

    public Spawn spawn(long currentTime) {
        lastSpawned = currentTime;
        return new Spawn(position, direction, speed);
    }

    public boolean shouldSpawn(long currentTime) {
        return alive
                && activate < currentTime
                && (lastSpawned + rate < currentTime || lastSpawned == 0);
    }

    public class Spawn {
        private final int position;
        private final int direction;
        private final int speed;

        Spawn(int position, int direction, int speed) {
            this.position = position;
            this.direction = direction;
            this.speed = speed;
        }

        public int getPosition() {
            return position;
        }

        public int getDirection() {
            return direction;
        }

        public int getSpeed() {
            return speed;
        }
    }
}
