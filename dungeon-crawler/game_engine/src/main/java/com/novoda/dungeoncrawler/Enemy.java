package com.novoda.dungeoncrawler;

public class Enemy {
    private final int speed;
    private final int direction;
    private final int wobble;
    private final int playerSide;
    private final int origin;

    private int position;
    private boolean alive;

    Enemy(int position, int direction, int speed, int wobble, int playerSide) {
        this.position = position;
        this.direction = direction; // 0 = left, 1 = right
        this.speed = speed;
        this.wobble = wobble; // 0 = no, >0 = yes, value is width of wobble
        this.origin = position;
        this.playerSide = playerSide;
        this.alive = true;
    }

    boolean hitPlayer(int playerPosition) {
        return (playerSide == 1 && position <= playerPosition) || (playerSide == -1 && position >= playerPosition);
    }

    boolean hitAttack(int attackStartPosition, int attackEndPosition) {
        return position > attackStartPosition && position < attackEndPosition;
    }

    boolean isAlive() {
        return alive;
    }

    void tick(long millis) {
        if (alive) {
            if (wobble > 0) {
                position = (int) (origin + (Math.sin((millis / 3000.0) * speed) * wobble));
            } else {
                if (direction == 0) {
                    position -= speed;
                } else {
                    position += speed;
                }
                if (position > 1000) {
                    kill();
                }
                if (position <= 0) {
                    kill();
                }
            }

        }
    }

    void kill() {
        alive = false;
    }

    public int getPosition() {
        return position;
    }
}
