package com.novoda.dungeoncrawler;

import java.util.Random;

public class Particle {
    public int position;
    public int power;
    private boolean alive;
    private int sp;
    private int life;

    public void kill() {
        this.alive = false;
    }

    public void spawn(int playerPosition) {
        this.position = playerPosition;
        this.power = 255;
        this.alive = true;
        this.sp = new Random().nextInt(400) + -200;
        this.life = 220 - Math.abs(sp);
    }

    public boolean isAlive() {
        return alive;
    }

    public void tick(boolean useGravity) {
        if (alive) {
            life++;
            if (sp > 0) {
                sp -= life / 10;
            } else {
                sp += life / 10;
            }
            if (useGravity && position > 500) {
                sp -= 10;
            }
            power = 100 - life;
            if (power <= 0) {
                kill();
            } else {
                position += sp / 7.0;
                if (position > 1000) {
                    position = 1000;
                    sp = 0 - (sp / 2);
                } else if (position < 0) {
                    position = 0;
                    sp = 0 - (sp / 2);
                }
            }
        }
    }
}
