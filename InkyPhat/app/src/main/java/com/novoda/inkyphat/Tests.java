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
        activity.setPixel(3, 3, Palette.BLACK);
        activity.setPixel(4, 3, Palette.RED);
        activity.setPixel(5, 3, Palette.BLACK);
        activity.setPixel(6, 3, Palette.RED);
        activity.setPixel(7, 3, Palette.BLACK);
        activity.setPixel(8, 3, Palette.RED);
        activity.setPixel(9, 3, Palette.BLACK);

        activity.setPixel(3, 3, Palette.RED);
        activity.setPixel(3, 4, Palette.BLACK);
        activity.setPixel(3, 5, Palette.RED);
        activity.setPixel(3, 6, Palette.BLACK);
        activity.setPixel(3, 7, Palette.RED);
        activity.setPixel(3, 8, Palette.BLACK);
        activity.setPixel(3, 9, Palette.RED);

        activity.setPixel(9, 3, Palette.BLACK);
        activity.setPixel(9, 4, Palette.RED);
        activity.setPixel(9, 5, Palette.BLACK);
        activity.setPixel(9, 6, Palette.RED);
        activity.setPixel(9, 7, Palette.BLACK);
        activity.setPixel(9, 8, Palette.RED);
        activity.setPixel(9, 9, Palette.BLACK);

        activity.setPixel(3, 9, Palette.RED);
        activity.setPixel(4, 9, Palette.BLACK);
        activity.setPixel(5, 9, Palette.RED);
        activity.setPixel(6, 9, Palette.BLACK);
        activity.setPixel(7, 9, Palette.RED);
        activity.setPixel(8, 9, Palette.BLACK);
        activity.setPixel(9, 9, Palette.RED);

        // A square
        activity.setPixel(13, 13, Palette.BLACK);
        activity.setPixel(14, 13, Palette.BLACK);
        activity.setPixel(15, 13, Palette.BLACK);
        activity.setPixel(16, 13, Palette.BLACK);
        activity.setPixel(17, 13, Palette.BLACK);
        activity.setPixel(18, 13, Palette.BLACK);
        activity.setPixel(19, 13, Palette.BLACK);

        activity.setPixel(13, 13, Palette.BLACK);
        activity.setPixel(13, 14, Palette.BLACK);
        activity.setPixel(13, 15, Palette.BLACK);
        activity.setPixel(13, 16, Palette.BLACK);
        activity.setPixel(13, 17, Palette.BLACK);
        activity.setPixel(13, 18, Palette.BLACK);
        activity.setPixel(13, 19, Palette.BLACK);

        activity.setPixel(19, 13, Palette.BLACK);
        activity.setPixel(19, 14, Palette.BLACK);
        activity.setPixel(19, 15, Palette.BLACK);
        activity.setPixel(19, 16, Palette.BLACK);
        activity.setPixel(19, 17, Palette.BLACK);
        activity.setPixel(19, 18, Palette.BLACK);
        activity.setPixel(19, 19, Palette.BLACK);

        activity.setPixel(13, 19, Palette.BLACK);
        activity.setPixel(14, 19, Palette.BLACK);
        activity.setPixel(15, 19, Palette.BLACK);
        activity.setPixel(16, 19, Palette.BLACK);
        activity.setPixel(17, 19, Palette.BLACK);
        activity.setPixel(18, 19, Palette.BLACK);
        activity.setPixel(19, 19, Palette.BLACK);

        // A square
        activity.setPixel(23, 23, Palette.RED);
        activity.setPixel(24, 23, Palette.BLACK);
        activity.setPixel(25, 23, Palette.WHITE);
        activity.setPixel(26, 23, Palette.RED);
        activity.setPixel(27, 23, Palette.BLACK);
        activity.setPixel(28, 23, Palette.WHITE);
        activity.setPixel(29, 23, Palette.RED);

        activity.setPixel(23, 23, Palette.RED);
        activity.setPixel(23, 24, Palette.BLACK);
        activity.setPixel(23, 25, Palette.WHITE);
        activity.setPixel(23, 26, Palette.RED);
        activity.setPixel(23, 27, Palette.BLACK);
        activity.setPixel(23, 28, Palette.WHITE);
        activity.setPixel(23, 29, Palette.RED);

        activity.setPixel(29, 23, Palette.RED);
        activity.setPixel(29, 24, Palette.BLACK);
        activity.setPixel(29, 25, Palette.WHITE);
        activity.setPixel(29, 26, Palette.RED);
        activity.setPixel(29, 27, Palette.BLACK);
        activity.setPixel(29, 28, Palette.WHITE);
        activity.setPixel(29, 29, Palette.RED);

        activity.setPixel(23, 29, Palette.RED);
        activity.setPixel(24, 29, Palette.BLACK);
        activity.setPixel(25, 29, Palette.WHITE);
        activity.setPixel(26, 29, Palette.RED);
        activity.setPixel(27, 29, Palette.BLACK);
        activity.setPixel(28, 29, Palette.WHITE);
        activity.setPixel(29, 29, Palette.RED);
    }

}
