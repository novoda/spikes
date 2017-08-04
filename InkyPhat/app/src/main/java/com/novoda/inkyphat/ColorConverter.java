package com.novoda.inkyphat;

import android.graphics.Color;

class ColorConverter {

    /**
     * range is 0-255
     */
    private static final int THRESHOLD_BLACK = 85;
    /**
     * range is 0-255
     */
    private static final int THRESHOLD_RED = 40;

    InkyPhat.Palette convertARBG888Color(int color) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        if (red > 127 && blue > 127 && green > 127) {
            return InkyPhat.Palette.WHITE;
        }

        if (red > 127) {
            return InkyPhat.Palette.RED;
        }

        return InkyPhat.Palette.BLACK;
    }

    InkyPhat.Palette convertAlpha8Color(int color) {
        int alpha = Color.alpha(color);
        InkyPhat.Palette palette;
        if (alpha > THRESHOLD_RED) {
            palette = InkyPhat.Palette.RED;
        } else if (alpha > THRESHOLD_BLACK) {
            palette = InkyPhat.Palette.BLACK;
        } else {
            palette = InkyPhat.Palette.WHITE;
        }
        return palette;
    }
}
