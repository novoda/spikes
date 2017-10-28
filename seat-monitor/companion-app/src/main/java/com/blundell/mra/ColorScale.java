package com.blundell.mra;

import android.graphics.Color;

/**
 * Thanks https://stackoverflow.com/questions/4414673/android-color-between-two-colors-based-on-percentage
 */
class ColorScale {
    private static int FIRST_COLOR = Color.BLUE;
    private static int SECOND_COLOR = Color.RED;
    private static int THIRD_COLOR = Color.RED;

    static int getColor(float percent) {
        int c0;
        int c1;
        if (percent <= 50) {
            c0 = FIRST_COLOR;
            c1 = SECOND_COLOR;
        } else {
            c0 = SECOND_COLOR;
            c1 = THIRD_COLOR;
        }
        int r = ave(Color.red(c0), Color.red(c1), percent);
        int g = ave(Color.green(c0), Color.green(c1), percent);
        int b = ave(Color.blue(c0), Color.blue(c1), percent);
        return Color.rgb(r, g, b);
    }

    private static int ave(int src, int dst, float p) {
        return src + java.lang.Math.round(p * (dst - src));
    }
}
