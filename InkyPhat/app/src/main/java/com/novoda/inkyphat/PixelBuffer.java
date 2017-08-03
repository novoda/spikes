package com.novoda.inkyphat;

import com.novoda.inkyphat.InkyPhat.Palette;

class PixelBuffer {

    private static final int PIXELS_PER_REGION = 8;
    private static final int NUMBER_OF_PIXEL_REGIONS = InkyPhat.WIDTH * InkyPhat.HEIGHT / PIXELS_PER_REGION;

    private final Palette[][] pixelBuffer = new Palette[InkyPhat.WIDTH][InkyPhat.HEIGHT];

    void setPixel(int x, int y, Palette color) {
        if (x > InkyPhat.WIDTH) {
            throw new IllegalStateException(x + " cannot be drawn. Max width is " + InkyPhat.WIDTH);
        }
        if (y > InkyPhat.HEIGHT) {
            throw new IllegalStateException(y + " cannot be drawn. Max height is " + InkyPhat.HEIGHT);
        }
        pixelBuffer[x][y] = color;
    }

    byte[] getDisplayPixelsForColor(Palette color) {
        return mapPaletteArrayToDisplayByteArray(flatten(pixelBuffer), color);
    }

    private static Palette[] flatten(Palette[][] twoDimensionalPaletteArray) {
        Palette[] flattenedArray = new Palette[InkyPhat.WIDTH * InkyPhat.HEIGHT];
        int index = 0;
        for (int y = 0; y < InkyPhat.HEIGHT; y++) {
            for (int x = 0; x < InkyPhat.WIDTH; x++) {
                Palette color = twoDimensionalPaletteArray[x][y];
                flattenedArray[index++] = color;
            }
        }
        return flattenedArray;
    }

    /**
     * Every 8 pixels of the display is represented by a byte
     *
     * @param palette an array colors expecting to be drawn
     * @param choice  the color we are filtering for
     * @return a byte array representing the palette of a single color
     */
    private static byte[] mapPaletteArrayToDisplayByteArray(Palette[] palette, Palette choice) {
        byte[] display = new byte[NUMBER_OF_PIXEL_REGIONS];
        int bitPosition = 7;
        int segment = 0;
        byte colorByte = 0b00000000;
        for (Palette color : palette) {
            if (color == choice) {
                colorByte |= 1 << bitPosition;
            }
            bitPosition--;
            if (bitPosition == -1) {
                display[segment++] = colorByte;
                bitPosition = 7;
                colorByte = 0b00000000;
            }
        }
        return display;
    }

}
