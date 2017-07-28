package com.novoda.inkyphat;

import com.novoda.inkyphat.MainActivity.Palette;

public class Tests {

    public static void drawWholeScreen(MainActivity activity) {
        for (int row = 0; row < MainActivity.HEIGHT; row++) {
            for (int col = 0; col < MainActivity.WIDTH; col++) {
                activity.setPixel(row, col, Palette.WHITE);
            }
        }
    }

    public static void drawThreeSquares(MainActivity activity) {
        // A square
        activity.setPixel(3, 3, Palette.RED);
        activity.setPixel(4, 3, Palette.BLACK);
        activity.setPixel(5, 3, Palette.BLACK);
        activity.setPixel(6, 3, Palette.BLACK);
        activity.setPixel(7, 3, Palette.BLACK);
        activity.setPixel(8, 3, Palette.BLACK);
        activity.setPixel(9, 3, Palette.RED);

        activity.setPixel(3, 3, Palette.BLACK);
        activity.setPixel(3, 4, Palette.BLACK);
        activity.setPixel(3, 5, Palette.BLACK);
        activity.setPixel(3, 6, Palette.BLACK);
        activity.setPixel(3, 7, Palette.BLACK);
        activity.setPixel(3, 8, Palette.BLACK);
        activity.setPixel(3, 9, Palette.BLACK);
//
        activity.setPixel(9, 3, Palette.BLACK);
        activity.setPixel(9, 4, Palette.BLACK);
        activity.setPixel(9, 5, Palette.BLACK);
        activity.setPixel(9, 6, Palette.BLACK);
        activity.setPixel(9, 7, Palette.BLACK);
        activity.setPixel(9, 8, Palette.BLACK);
        activity.setPixel(9, 9, Palette.BLACK);
//
        activity.setPixel(3, 9, Palette.BLACK);
        activity.setPixel(4, 9, Palette.BLACK);
        activity.setPixel(5, 9, Palette.BLACK);
        activity.setPixel(6, 9, Palette.BLACK);
        activity.setPixel(7, 9, Palette.BLACK);
        activity.setPixel(8, 9, Palette.BLACK);
        activity.setPixel(9, 9, Palette.BLACK);
//
//        // A square
        activity.setPixel(23, 23, Palette.RED);
        activity.setPixel(24, 23, Palette.RED);
        activity.setPixel(25, 23, Palette.RED);
        activity.setPixel(26, 23, Palette.RED);
        activity.setPixel(27, 23, Palette.RED);
        activity.setPixel(28, 23, Palette.RED);
        activity.setPixel(29, 23, Palette.RED);

        activity.setPixel(23, 23, Palette.RED);
        activity.setPixel(23, 24, Palette.RED);
        activity.setPixel(23, 25, Palette.RED);
        activity.setPixel(23, 26, Palette.RED);
        activity.setPixel(23, 27, Palette.RED);
        activity.setPixel(23, 28, Palette.RED);
        activity.setPixel(23, 29, Palette.RED);

        activity.setPixel(29, 23, Palette.RED);
        activity.setPixel(29, 24, Palette.RED);
        activity.setPixel(29, 25, Palette.RED);
        activity.setPixel(29, 26, Palette.RED);
        activity.setPixel(29, 27, Palette.RED);
        activity.setPixel(29, 28, Palette.RED);
        activity.setPixel(29, 29, Palette.RED);

        activity.setPixel(23, 29, Palette.RED);
        activity.setPixel(24, 29, Palette.RED);
        activity.setPixel(25, 29, Palette.RED);
        activity.setPixel(26, 29, Palette.RED);
        activity.setPixel(27, 29, Palette.RED);
        activity.setPixel(28, 29, Palette.RED);
        activity.setPixel(29, 29, Palette.RED);
    }

}
