package com.novoda.dungeoncrawler;

class Enemy {
    public int playerSide;
    public int position;
    private boolean alive;
    private int speed;
    private int direction;
    private int wobble;
    private int origin;

    void spawn(int pos, int dir, int speed, int wobble) {
        position = pos;
        direction = dir;          // 0 = left, 1 = right
        this.wobble = wobble;    // 0 = no, >0 = yes, value is width of wobble
        origin = pos;
        this.speed = speed;
        alive = true;
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

}
