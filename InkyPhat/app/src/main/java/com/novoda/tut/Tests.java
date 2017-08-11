package com.novoda.tut;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import com.novoda.inkyphat.InkyPhat;
import com.novoda.inkyphat.R;
import com.novoda.inkyphat.ShapeDrawer;

class Tests {

    static void drawWholeScreen(InkyPhat inkyPhat) {
        for (int row = 0; row < InkyPhat.HEIGHT; row++) {
            for (int col = 0; col < InkyPhat.WIDTH; col++) {
                inkyPhat.setPixel(row, col, Color.WHITE);
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
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_test_1);
        inkyPhat.setBorder(InkyPhat.Palette.RED);
        inkyPhat.setImage(0, 0, bitmap, Matrix.ScaleToFit.START);
    }

    static void drawSmiley(InkyPhat inkyPhat) {
        ShapeDrawer shapeDrawer = new ShapeDrawer(inkyPhat); // TODO invert dependency

        // Left eye
        shapeDrawer.drawCircle(40, 40, 20, Color.BLACK);
        shapeDrawer.drawCircle(40, 40, 5, Color.BLACK);
        // Right eye
        shapeDrawer.drawCircle(80, 40, 20, Color.BLACK);
        shapeDrawer.drawCircle(80, 40, 5, Color.BLACK);
        // Nose
        shapeDrawer.drawCircle(60, 75, 15, Color.BLACK);
        // Mouth
        shapeDrawer.drawArc(60, 90, 25, 0, 180, Color.BLACK);
    }

    static void drawHelloWorldText(InkyPhat inkyPhat) {
      inkyPhat.setText(0, 0, "Hello World", Color.WHITE);
    }

    static void drawNameBadge(InkyPhat inkyPhat, Resources resources) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_test_3);
        inkyPhat.setBorder(InkyPhat.Palette.RED);
        inkyPhat.setImage(0, 0, bitmap, Matrix.ScaleToFit.START);
        inkyPhat.setText(50, 65, "Ataul Munim", Color.BLACK);
    }
}
