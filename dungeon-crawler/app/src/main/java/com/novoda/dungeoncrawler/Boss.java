package com.novoda.dungeoncrawler;

public class Boss {
    public int lives;
    public int position;
    public int ticks;
    private boolean alive;

    public void spawn() {
        position = 800;
        lives = 3;
        alive = true;
    }

    public void hit() {
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

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }
}
