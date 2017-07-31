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
import android.util.Log;

import static com.novoda.inkyphat.InkyPhat.HEIGHT;

public class ImageDrawer {

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
        String line = "";
        int pixelCount = 0;
        InkyPhat.Palette[] onOrOff = new InkyPhat.Palette[width * height];
        for (int i = 0, pixelsLength = pixels.length; i < pixelsLength; i++) {
            int pixel = pixels[i];
            int alpha = Color.alpha(pixel);
            if (alpha > 85) {
                line += "X";
                onOrOff[i] = InkyPhat.Palette.BLACK;
            } else if (alpha > 40) {
                line += "Y";
                onOrOff[i] = InkyPhat.Palette.RED;
            } else {
                line += "-";
                onOrOff[i] = InkyPhat.Palette.WHITE;
            }
            pixelCount++;
            if (pixelCount == width) {
                Log.d("TUT", line);
                line = "";
                pixelCount = 0;
            }
        }
        return new InkyPhat.PaletteImage(onOrOff, width);
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
        Bitmap bitmap;
        int bitmapWidth = sourceBitmap.getWidth();
        int bitmapHeight = sourceBitmap.getHeight();
        if (bitmapWidth > InkyPhat.WIDTH || bitmapHeight > HEIGHT) { // TODO add a param for orientation
            //noinspection SuspiciousNameCombination
            bitmap = scaleBitmap(sourceBitmap, InkyPhat.HEIGHT, InkyPhat.WIDTH);
        } else {
            bitmap = sourceBitmap.copy(Bitmap.Config.ALPHA_8, true);
        }
        return bitmap;
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
