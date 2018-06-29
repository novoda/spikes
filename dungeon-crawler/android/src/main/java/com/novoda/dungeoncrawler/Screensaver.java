package com.novoda.dungeoncrawler;

import java.util.Random;

public class Screensaver {

    private final Display display;
    private final int numOfSquares;

    public Screensaver(Display display, int numOfSquares) {
        this.display = display;
        this.numOfSquares = numOfSquares;
    }

    void draw(long frameTime) {
        int n, b, c, i;
        int mode = (int) ((frameTime / 20000) % 2);

        for (i = 0; i < numOfSquares; i++) {
            display.modifyScale(i, 250);
        }
        if (mode == 0) {
            // Marching green <> orange
            n = (int) ((frameTime / 250) % 10);
            b = (int) (10 + ((Math.sin(frameTime / 500.00) + 1) * 20.00));
            c = (int) (20 + ((Math.sin(frameTime / 5000.00) + 1) * 33));
            for (i = 0; i < numOfSquares; i++) {
                if (i % 10 == n) {
                    display.set(i, new Display.CHSV(b, 255, 150));
                } else {
                    display.set(i, new Display.CHSV(c, 255, 75));
                }
            }
        } else if (mode == 1) {
            // Random flashes
            Random random = new Random(frameTime);
            for (i = 0; i < numOfSquares; i++) {
                if (random.nextInt(200) == 0) {
                    display.set(i, new Display.CHSV(25, 255, 100));
                }
            }
        }
    }

}
