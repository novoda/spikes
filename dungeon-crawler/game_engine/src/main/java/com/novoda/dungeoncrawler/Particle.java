package com.novoda.dungeoncrawler;

import java.util.Random;

public class Particle {
    private boolean alive;
    private int position;
    private int power;
    private int speed;
    private int life;

    void spawn(int playerPosition) {
        this.position = playerPosition;
        this.power = 255;
        this.alive = true;
        this.speed = new Random().nextInt(400) + -200;
        this.life = 220 - Math.abs(speed);
    }

    public boolean isAlive() {
        return alive;
    }

    void tick(boolean useGravity) {
        if (alive) {
            life++;
            if (speed > 0) {
                speed -= life / 10;
            } else {
                speed += life / 10;
            }
            if (useGravity && position > 500) {
                speed -= 10;
            }
            power = 100 - life;
            if (getPower() <= 0) {
                kill();
            } else {
                position += speed / 7.0;
                if (position > 1000) {
                    position = 1000;
                    speed = 0 - (speed / 2);
                } else if (position < 0) {
                    position = 0;
                    speed = 0 - (speed / 2);
                }
            }
        }
    }

    private void kill() {
        this.alive = false;
    }

    public int getPosition() {
        return position;
    }

    public int getPower() {
        return power;
    }
}
