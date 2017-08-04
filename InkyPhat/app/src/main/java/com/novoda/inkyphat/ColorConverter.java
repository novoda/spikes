package com.novoda.inkyphat;

import android.graphics.Color;

class ColorConverter {

    InkyPhat.Palette convertColor(int color) {
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
}
