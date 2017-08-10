package com.novoda.inkyphat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class TestActivity extends Activity {

    private static final InkyPhat.Orientation ORIENTATION = InkyPhat.Orientation.PORTRAIT;
    private static final int WIDTH = (ORIENTATION == InkyPhat.Orientation.PORTRAIT) ? InkyPhat.WIDTH : InkyPhat.HEIGHT;
    private static final int HEIGHT = (ORIENTATION == InkyPhat.Orientation.PORTRAIT) ? InkyPhat.HEIGHT : InkyPhat.WIDTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        LinearLayout root = (LinearLayout) findViewById(R.id.root);
        ImageConverter imageConverter = new ImageConverter(ORIENTATION);
        Bitmap input = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_1);
        Bitmap result = imageConverter.filterImage(input, Matrix.ScaleToFit.START);
        showOriginalInDebugView(root, input);
        showInDebugView(root, result);
    }

    private void showOriginalInDebugView(LinearLayout root, Bitmap image) {
        ImageView imageview = new ImageView(this);
        LayoutParams params;
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = 10;
        params.topMargin = 10;
        params.rightMargin = 10;
        params.bottomMargin = 10;
//            params.gravity = Gravity.CENTER;
        imageview.setLayoutParams(params);
        imageview.setImageBitmap(image);
        imageview.setBackgroundColor(Color.BLUE);
        root.addView(imageview);
    }

    private void showInDebugView(LinearLayout root, Bitmap image) {
        ImageView imageview = new ImageView(this);
        LayoutParams params;
        params = new LayoutParams(WIDTH, HEIGHT);
        params.leftMargin = 10;
        params.topMargin = 10;
        params.rightMargin = 10;
        params.bottomMargin = 10;
//            params.gravity = Gravity.CENTER;
        imageview.setLayoutParams(params);
        imageview.setImageBitmap(image);
        imageview.setBackgroundColor(Color.BLUE);
        root.addView(imageview);
    }
}
