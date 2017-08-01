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

    public ImageDrawer(InkyPhat.Orientation orientation) {
        this(orientation, Matrix.ScaleToFit.FILL);
    }

    public ImageDrawer(InkyPhat.Orientation orientation, Matrix.ScaleToFit scaleToFit) {
        this.orientation = orientation;
        this.scaleToFit = scaleToFit;
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
        Bitmap scaled = scaleToInkyPhatBounds(sourceBitmap);
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

    private Bitmap scaleToInkyPhatBounds(Bitmap sourceBitmap) {
        int bitmapWidth = getOrientatedWidth(sourceBitmap);
        int bitmapHeight = getOrientatedHeight(sourceBitmap);
        if (bitmapWidth < InkyPhat.WIDTH && bitmapHeight < InkyPhat.HEIGHT) {
            return sourceBitmap;
        }

        switch (scaleToFit) {
            case FILL:
                return fitXY(sourceBitmap);
            case START:
            case CENTER:
            case END:
                return fitXorY(sourceBitmap);
            default:
                throw new IllegalStateException("Unsupported scale type of " + scaleToFit);
        }
    }

    private int getOrientatedWidth(Bitmap sourceBitmap) {
        return isIn(PORTRAIT) ? sourceBitmap.getWidth() : sourceBitmap.getHeight();
    }

    private int getOrientatedHeight(Bitmap sourceBitmap) {
        return isIn(LANDSCAPE) ? sourceBitmap.getHeight() : sourceBitmap.getWidth();
    }

    private Bitmap fitXY(Bitmap sourceBitmap) {
        int bitmapWidth = sourceBitmap.getWidth();
        int bitmapHeight = sourceBitmap.getHeight();
        if (isIn(PORTRAIT) && (bitmapWidth > InkyPhat.WIDTH || bitmapHeight > InkyPhat.HEIGHT)) {
            return scaleBitmap(sourceBitmap, InkyPhat.WIDTH, InkyPhat.HEIGHT);
        } else if (isIn(LANDSCAPE) && (bitmapWidth > InkyPhat.HEIGHT || bitmapHeight > InkyPhat.WIDTH)) {
            //noinspection SuspiciousNameCombination
            return scaleBitmap(sourceBitmap, InkyPhat.HEIGHT, InkyPhat.WIDTH);
        } else {
            return sourceBitmap.copy(Bitmap.Config.ALPHA_8, true);
        }
    }

    private Bitmap fitXorY(Bitmap sourceBitmap) {
        int bitmapWidth = sourceBitmap.getWidth();
        int bitmapHeight = sourceBitmap.getHeight();

        int diffWidth = bitmapWidth - InkyPhat.WIDTH;
        int diffHeight = bitmapHeight - InkyPhat.HEIGHT;

        if (diffWidth >= diffHeight) {
            // scale by width
            double widthScaleVector = InkyPhat.WIDTH / (double) bitmapWidth;
            int wantedWidth = InkyPhat.WIDTH;
            int wantedHeight = (int) (bitmapHeight * widthScaleVector);
            return scaleBitmap(sourceBitmap, wantedWidth, wantedHeight);
        } else {
            // scale by height
            double heightScaleVector = InkyPhat.HEIGHT / (double) bitmapHeight;
            int wantedWidth = (int) (bitmapWidth * heightScaleVector);
            int wantedHeight = InkyPhat.HEIGHT;
            return scaleBitmap(sourceBitmap, wantedWidth, wantedHeight);
        }
    }

    private boolean isIn(InkyPhat.Orientation orientation) {
        return this.orientation == orientation;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix resizeMatrix = new Matrix();
        resizeMatrix.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, resizeMatrix, new Paint());

        return output;
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
