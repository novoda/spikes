package com.novoda.tut;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.novoda.inkyphat.InkyPhat;
import com.novoda.inkyphat.R;

class Tests {

    static void drawWholeScreen(InkyPhat inkyPhat) {
        for (int row = 0; row < InkyPhat.HEIGHT; row++) {
            for (int col = 0; col < InkyPhat.WIDTH; col++) {
                inkyPhat.setPixel(row, col, Color.BLACK);
            }
        }
    }

    static void drawTwoSquares(InkyPhat inkyPhat) {
        // A square
        inkyPhat.setPixel(3, 3, Color.BLACK);
        inkyPhat.setPixel(4, 3, Color.BLACK);
        inkyPhat.setPixel(5, 3, Color.BLACK);
        inkyPhat.setPixel(6, 3, Color.BLACK);
        inkyPhat.setPixel(7, 3, Color.BLACK);
        inkyPhat.setPixel(8, 3, Color.BLACK);
        inkyPhat.setPixel(9, 3, Color.BLACK);

        inkyPhat.setPixel(3, 3, Color.BLACK);
        inkyPhat.setPixel(3, 4, Color.BLACK);
        inkyPhat.setPixel(3, 5, Color.BLACK);
        inkyPhat.setPixel(3, 6, Color.BLACK);
        inkyPhat.setPixel(3, 7, Color.BLACK);
        inkyPhat.setPixel(3, 8, Color.BLACK);
        inkyPhat.setPixel(3, 9, Color.BLACK);

        inkyPhat.setPixel(9, 3, Color.BLACK);
        inkyPhat.setPixel(9, 4, Color.BLACK);
        inkyPhat.setPixel(9, 5, Color.BLACK);
        inkyPhat.setPixel(9, 6, Color.BLACK);
        inkyPhat.setPixel(9, 7, Color.BLACK);
        inkyPhat.setPixel(9, 8, Color.BLACK);
        inkyPhat.setPixel(9, 9, Color.BLACK);

        inkyPhat.setPixel(3, 9, Color.BLACK);
        inkyPhat.setPixel(4, 9, Color.BLACK);
        inkyPhat.setPixel(5, 9, Color.BLACK);
        inkyPhat.setPixel(6, 9, Color.BLACK);
        inkyPhat.setPixel(7, 9, Color.BLACK);
        inkyPhat.setPixel(8, 9, Color.BLACK);
        inkyPhat.setPixel(9, 9, Color.BLACK);

        // A square
        inkyPhat.setPixel(23, 23, Color.RED);
        inkyPhat.setPixel(24, 23, Color.RED);
        inkyPhat.setPixel(25, 23, Color.RED);
        inkyPhat.setPixel(26, 23, Color.RED);
        inkyPhat.setPixel(27, 23, Color.RED);
        inkyPhat.setPixel(28, 23, Color.RED);
        inkyPhat.setPixel(29, 23, Color.RED);

        inkyPhat.setPixel(23, 23, Color.RED);
        inkyPhat.setPixel(23, 24, Color.RED);
        inkyPhat.setPixel(23, 25, Color.RED);
        inkyPhat.setPixel(23, 26, Color.RED);
        inkyPhat.setPixel(23, 27, Color.RED);
        inkyPhat.setPixel(23, 28, Color.RED);
        inkyPhat.setPixel(23, 29, Color.RED);

        inkyPhat.setPixel(29, 23, Color.RED);
        inkyPhat.setPixel(29, 24, Color.RED);
        inkyPhat.setPixel(29, 25, Color.RED);
        inkyPhat.setPixel(29, 26, Color.RED);
        inkyPhat.setPixel(29, 27, Color.RED);
        inkyPhat.setPixel(29, 28, Color.RED);
        inkyPhat.setPixel(29, 29, Color.RED);

        inkyPhat.setPixel(23, 29, Color.RED);
        inkyPhat.setPixel(24, 29, Color.RED);
        inkyPhat.setPixel(25, 29, Color.RED);
        inkyPhat.setPixel(26, 29, Color.RED);
        inkyPhat.setPixel(27, 29, Color.RED);
        inkyPhat.setPixel(28, 29, Color.RED);
        inkyPhat.setPixel(29, 29, Color.RED);
    }

    static void drawImage(InkyPhat inkyPhat, Resources resources) {
        inkyPhat.setBorder(InkyPhat.Palette.RED);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_test_4);
        inkyPhat.setImage(0, 0, bitmap, InkyPhat.Scale.FIT_X_OR_Y);
    }

    static void drawSmiley(InkyPhat inkyPhat) {
        Bitmap image = Bitmap.createBitmap(InkyPhat.WIDTH, InkyPhat.HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(0xFFFFFFFF);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(40, 40, 20, paint);

        // Left eye
        canvas.drawCircle(40, 40, 20, paint);
        // Right eye
        canvas.drawCircle(80, 40, 20, paint);
        // Nose
        canvas.drawCircle(60, 75, 15, paint);
        // Mouth
        canvas.drawArc(20, 90, 100, 120, 0, 180, false, paint);

        inkyPhat.setImage(0, 0, image, InkyPhat.Scale.FIT_X_OR_Y);
    }

    static void drawHelloWorldText(InkyPhat inkyPhat) {
        inkyPhat.setText(0, 0, "Hello World", Color.WHITE);
    }

    static void drawNameBadge(InkyPhat inkyPhat, Resources resources) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_test_3);
        inkyPhat.setBorder(InkyPhat.Palette.RED);
        inkyPhat.setImage(0, 0, bitmap, InkyPhat.Scale.FIT_X_OR_Y);
        inkyPhat.setText(50, 65, "Ataul Munim", Color.BLACK);
    }
}
