package com.novoda.inkyphat;

import android.util.Log;

import com.novoda.inkyphat.InkyPhat.Palette;

import static com.novoda.inkyphat.InkyPhat.Orientation.LANDSCAPE;
import static com.novoda.inkyphat.InkyPhat.Orientation.PORTRAIT;

class PixelBuffer {

    private static final int PIXELS_PER_REGION = 8;
    private static final int NUMBER_OF_PIXEL_REGIONS = InkyPhat.WIDTH * InkyPhat.HEIGHT / PIXELS_PER_REGION;

    private final Palette[][] pixelBuffer;
    private final InkyPhat.Orientation orientation;

    PixelBuffer(InkyPhat.Orientation orientation) {
        this.orientation = orientation;
        pixelBuffer = new Palette[getOrientatedWidth()][getOrientatedHeight()];
    }

    private int getOrientatedWidth() {
        return isIn(PORTRAIT) ? InkyPhat.WIDTH : InkyPhat.HEIGHT;
    }

    private int getOrientatedHeight() {
        return isIn(LANDSCAPE) ? InkyPhat.HEIGHT : InkyPhat.WIDTH;
    }

    private boolean isIn(InkyPhat.Orientation orientation) {
        return this.orientation == orientation;
    }

    void setImage(int x, int y, InkyPhat.PaletteImage image) {
        int rowCount = 0;
        int pixelCount = 0;
        for (int i = 0; i < image.totalPixels(); i++) {
            int localX = x + i;
            int localY = y + i + rowCount;

            if (localX > getOrientatedWidth() || localY > getOrientatedHeight()) {
                continue;
            }

            InkyPhat.Palette color = image.getPixel(i);
            setPixel(localX, localY, color);

            pixelCount++;
            if (pixelCount == image.getWidth()) {
                rowCount++;
                pixelCount = 0;
            }
        }
    }

    void setPixel(int x, int y, Palette color) {
        if (x < 0 || x >= getOrientatedWidth()) {
            Log.v("InkyPhat", "Attempt to draw outside of X bounds (x:" + x + " y:" + y + ")");
            return;
        }
        if (y < 0 || y >= getOrientatedHeight()) {
            Log.v("InkyPhat", "Attempt to draw outside of Y bounds (x:" + x + " y:" + y + ")");
            return;
        }
        pixelBuffer[x][y] = color;
    }

    byte[] getDisplayPixelsForColor(Palette color) {
        return mapPaletteArrayToDisplayByteArray(flatten(pixelBuffer), color);
    }

    private Palette[] flatten(Palette[][] twoDimensionalPaletteArray) {
        int width = getOrientatedWidth();
        int height = getOrientatedHeight();
        Palette[] flattenedArray = new Palette[width * height];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
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
