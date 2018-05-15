package com.novoda.dungeoncrawler;

class Boss {
    public int lives;
    public int position;
    public int ticks;
    private boolean alive;

    void spawn() {
        position = 800;
        lives = 3;
        alive = true;
    }

    void hit() {
        lives--;
        if (lives == 0) {
            kill();
            return;
        }
        if (lives == 2) {
            position = 200;
        } else if (lives == 1) {
            position = 600;
        }
    }

    boolean isAlive() {
        return alive;
    }

    void kill() {
        alive = false;
    }
}
