package com.novoda.inkyphat;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import static com.novoda.inkyphat.InkyPhat.Orientation.PORTRAIT;

class ImageConverter {

    private final InkyPhat.Orientation orientation;
    private final ImageScaler imageScaler;
    private final ColorConverter colorConverter;

    ImageConverter(InkyPhat.Orientation orientation) {
        this.orientation = orientation;
        this.imageScaler = new ImageScaler();
        this.colorConverter = new ColorConverter();
    }

    InkyPhat.PaletteImage convertImage(Bitmap input, Matrix.ScaleToFit scaleToFit) {
        return translateImage(filterImage(input, scaleToFit));
    }

    private InkyPhat.PaletteImage translateImage(Bitmap input) {
        int width = input.getWidth();
        int height = input.getHeight();
        int[] pixels = new int[width * height];
        input.getPixels(pixels, 0, width, 0, 0, width, height);
        int pixelCount = 0;
        InkyPhat.Palette[] colors = new InkyPhat.Palette[width * height];
        for (int i = 0, pixelsLength = pixels.length; i < pixelsLength; i++) {
            colors[i] = colorConverter.convertARBG888Color(pixels[i]);
            pixelCount++;
            if (pixelCount == width) {
                pixelCount = 0;
            }
        }
        return new InkyPhat.PaletteImage(colors, width);
    }

    Bitmap filterImage(Bitmap sourceBitmap, Matrix.ScaleToFit scaleToFit) {
        return scaleToInkyPhatBounds(sourceBitmap, scaleToFit);
    }

    private Bitmap scaleToInkyPhatBounds(Bitmap sourceBitmap, Matrix.ScaleToFit scaleType) {
        int bitmapWidth = sourceBitmap.getWidth();
        int bitmapHeight = sourceBitmap.getHeight();
        if (bitmapWidth < getOrientatedWidth() && bitmapHeight < getOrientatedHeight()) {
            return sourceBitmap;
        }

        switch (scaleType) {
            case FILL:
                return imageScaler.fitXY(sourceBitmap, getOrientatedWidth(), getOrientatedHeight());
            case START:
            case CENTER:
            case END:
                return imageScaler.fitXorY(sourceBitmap, getOrientatedWidth(), getOrientatedHeight());
            default:
                throw new IllegalStateException("Unsupported scale type of " + scaleType);
        }
    }

    private int getOrientatedWidth() {
        return isIn(PORTRAIT) ? InkyPhat.WIDTH : InkyPhat.HEIGHT;
    }

    private int getOrientatedHeight() {
        return isIn(PORTRAIT) ? InkyPhat.HEIGHT : InkyPhat.WIDTH;
    }

    private boolean isIn(InkyPhat.Orientation orientation) {
        return this.orientation == orientation;
    }

}
