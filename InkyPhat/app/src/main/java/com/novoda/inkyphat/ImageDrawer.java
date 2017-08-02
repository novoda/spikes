package com.novoda.inkyphat;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import static com.novoda.inkyphat.InkyPhat.Orientation.LANDSCAPE;
import static com.novoda.inkyphat.InkyPhat.Orientation.PORTRAIT;

public class ImageDrawer {

    /**
     * range is 0-255
     */
    private static final int THRESHOLD_BLACK = 85;
    /**
     * range is 0-255
     */
    private static final int THRESHOLD_RED = 40;

    private final InkyPhat.Orientation orientation;
    private final Matrix.ScaleToFit scaleToFit;
    private final ImageScaler imageScaler;

    public ImageDrawer(InkyPhat.Orientation orientation) {
        this(orientation, Matrix.ScaleToFit.FILL);
    }

    public ImageDrawer(InkyPhat.Orientation orientation, Matrix.ScaleToFit scaleToFit) {
        this.orientation = orientation;
        this.scaleToFit = scaleToFit;
        this.imageScaler = new ImageScaler();
    }

    public InkyPhat.PaletteImage drawImage(Resources resources, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
        return drawImage(bitmap);
    }

    public InkyPhat.PaletteImage drawImage(Bitmap input) {
        Bitmap[] bitmaps = filterImage(input);
        return convertImage(bitmaps[bitmaps.length - 1]);
    }

    InkyPhat.PaletteImage convertImage(Bitmap output) {
        int width = output.getWidth();
        int height = output.getHeight();
        int[] pixels = new int[width * height];
        output.getPixels(pixels, 0, width, 0, 0, width, height);
        int pixelCount = 0;
        InkyPhat.Palette[] colors = new InkyPhat.Palette[width * height];
        for (int i = 0, pixelsLength = pixels.length; i < pixelsLength; i++) {
            int pixel = pixels[i];
            int alpha = Color.alpha(pixel);
            if (alpha > THRESHOLD_RED) {
                colors[i] = InkyPhat.Palette.RED;
            } else if (alpha > THRESHOLD_BLACK) {
                colors[i] = InkyPhat.Palette.BLACK;
            } else {
                colors[i] = InkyPhat.Palette.WHITE;
            }
            pixelCount++;
            if (pixelCount == width) {
                pixelCount = 0;
            }
        }
        return new InkyPhat.PaletteImage(colors, width);
    }

    Bitmap[] filterImage(Bitmap sourceBitmap) {
        Bitmap scaled = scaleToInkyPhatBounds(sourceBitmap, scaleToFit);
        Bitmap filteredMono = filterToMonoChrome(scaled);
        Bitmap transparent = mapWhiteToTransparent(filteredMono);
        Bitmap filteredBlackWhite = filterToBlackAndWhite(transparent);
        return new Bitmap[]{sourceBitmap, scaled, filteredMono, transparent, filteredBlackWhite};
    }

    private Bitmap mapWhiteToTransparent(Bitmap sourceBitmap) {
        Bitmap output = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);

        int[] allPixels = new int[output.getHeight() * output.getWidth()];
        output.getPixels(allPixels, 0, output.getWidth(), 0, 0, output.getWidth(), output.getHeight());
        for (int i = 0; i < allPixels.length; i++) {
            float[] hsv = new float[3];
            Color.colorToHSV(allPixels[i], hsv);
            if (hsv[2] > 0.9f) {
                allPixels[i] = Color.TRANSPARENT;
            }
        }

        output.setPixels(allPixels, 0, output.getWidth(), 0, 0, output.getWidth(), output.getHeight());

        return output;
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
                throw new IllegalStateException("Unsupported scale type of " + scaleToFit);
        }
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

    private Bitmap filterToMonoChrome(Bitmap sourceBitmap) {
        Bitmap output = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
        ColorMatrix monochromeMatrix = new ColorMatrix();
        monochromeMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(monochromeMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(output, 0, 0, paint);
        return output;
    }

    private Bitmap filterToBlackAndWhite(Bitmap sourceBitmap) {
        return sourceBitmap.copy(Bitmap.Config.ALPHA_8, true);
    }

}
