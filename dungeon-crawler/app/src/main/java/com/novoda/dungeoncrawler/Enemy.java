package com.novoda.dungeoncrawler;

public class Enemy {
    public int playerSide;
    public int position;
    private boolean alive;
    private int _sp;
    private int direction;
    private int wobble;
    private int origin;

    public void spawn(int pos, int dir, int sp, int wobble) {
        position = pos;
        direction = dir;          // 0 = left, 1 = right
        this.wobble = wobble;    // 0 = no, >0 = yes, value is width of wobble
        origin = pos;
        _sp = sp;
        alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void tick(long millis) {
        if (alive) {
            if (wobble > 0) {
                position = (int) (origin + (Math.sin((millis / 3000.0) * _sp) * wobble));
            } else {
                if (direction == 0) {
                    position -= _sp;
                } else {
                    position += _sp;
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

    public void kill() {
        alive = false;
    }

}
