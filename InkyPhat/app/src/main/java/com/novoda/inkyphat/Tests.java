package com.novoda.inkyphat;

import com.novoda.inkyphat.InkyPhat.Palette;

class Tests {

    static void drawWholeScreen(InkyPhat inkyPhat) {
        for (int row = 0; row < InkyPhat.HEIGHT; row++) {
            for (int col = 0; col < InkyPhat.WIDTH; col++) {
                inkyPhat.setPixel(row, col, Palette.WHITE);
            }
        }
    }

    static void drawTwoSquares(InkyPhat inkyPhat) {
        // A square
        inkyPhat.setPixel(3, 3, Palette.BLACK);
        inkyPhat.setPixel(4, 3, Palette.BLACK);
        inkyPhat.setPixel(5, 3, Palette.BLACK);
        inkyPhat.setPixel(6, 3, Palette.BLACK);
        inkyPhat.setPixel(7, 3, Palette.BLACK);
        inkyPhat.setPixel(8, 3, Palette.BLACK);
        inkyPhat.setPixel(9, 3, Palette.BLACK);

        inkyPhat.setPixel(3, 3, Palette.BLACK);
        inkyPhat.setPixel(3, 4, Palette.BLACK);
        inkyPhat.setPixel(3, 5, Palette.BLACK);
        inkyPhat.setPixel(3, 6, Palette.BLACK);
        inkyPhat.setPixel(3, 7, Palette.BLACK);
        inkyPhat.setPixel(3, 8, Palette.BLACK);
        inkyPhat.setPixel(3, 9, Palette.BLACK);

        inkyPhat.setPixel(9, 3, Palette.BLACK);
        inkyPhat.setPixel(9, 4, Palette.BLACK);
        inkyPhat.setPixel(9, 5, Palette.BLACK);
        inkyPhat.setPixel(9, 6, Palette.BLACK);
        inkyPhat.setPixel(9, 7, Palette.BLACK);
        inkyPhat.setPixel(9, 8, Palette.BLACK);
        inkyPhat.setPixel(9, 9, Palette.BLACK);

        inkyPhat.setPixel(3, 9, Palette.BLACK);
        inkyPhat.setPixel(4, 9, Palette.BLACK);
        inkyPhat.setPixel(5, 9, Palette.BLACK);
        inkyPhat.setPixel(6, 9, Palette.BLACK);
        inkyPhat.setPixel(7, 9, Palette.BLACK);
        inkyPhat.setPixel(8, 9, Palette.BLACK);
        inkyPhat.setPixel(9, 9, Palette.BLACK);

        // A square
        inkyPhat.setPixel(23, 23, Palette.RED);
        inkyPhat.setPixel(24, 23, Palette.RED);
        inkyPhat.setPixel(25, 23, Palette.RED);
        inkyPhat.setPixel(26, 23, Palette.RED);
        inkyPhat.setPixel(27, 23, Palette.RED);
        inkyPhat.setPixel(28, 23, Palette.RED);
        inkyPhat.setPixel(29, 23, Palette.RED);

        inkyPhat.setPixel(23, 23, Palette.RED);
        inkyPhat.setPixel(23, 24, Palette.RED);
        inkyPhat.setPixel(23, 25, Palette.RED);
        inkyPhat.setPixel(23, 26, Palette.RED);
        inkyPhat.setPixel(23, 27, Palette.RED);
        inkyPhat.setPixel(23, 28, Palette.RED);
        inkyPhat.setPixel(23, 29, Palette.RED);

        inkyPhat.setPixel(29, 23, Palette.RED);
        inkyPhat.setPixel(29, 24, Palette.RED);
        inkyPhat.setPixel(29, 25, Palette.RED);
        inkyPhat.setPixel(29, 26, Palette.RED);
        inkyPhat.setPixel(29, 27, Palette.RED);
        inkyPhat.setPixel(29, 28, Palette.RED);
        inkyPhat.setPixel(29, 29, Palette.RED);

        inkyPhat.setPixel(23, 29, Palette.RED);
        inkyPhat.setPixel(24, 29, Palette.RED);
        inkyPhat.setPixel(25, 29, Palette.RED);
        inkyPhat.setPixel(26, 29, Palette.RED);
        inkyPhat.setPixel(27, 29, Palette.RED);
        inkyPhat.setPixel(28, 29, Palette.RED);
        inkyPhat.setPixel(29, 29, Palette.RED);
    }

}
