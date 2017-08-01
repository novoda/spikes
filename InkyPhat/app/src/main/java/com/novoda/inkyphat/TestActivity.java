package com.novoda.inkyphat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        LinearLayout root = (LinearLayout) findViewById(R.id.root);
        ImageDrawer imageDrawer = new ImageDrawer(InkyPhat.Orientation.LANDSCAPE, Matrix.ScaleToFit.START);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_4);
        Bitmap[] foo = imageDrawer.filterImage(bitmap);
        for (Bitmap image : foo) {
            ImageView imageview = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 10;
            params.topMargin = 10;
            params.rightMargin = 10;
            params.bottomMargin = 10;
            params.gravity = Gravity.CENTER;
            imageview.setLayoutParams(params);
            imageview.setImageBitmap(image);
            imageview.setBackgroundColor(Color.BLUE);
            root.addView(imageview);
        }

        InkyPhat.PaletteImage output = imageDrawer.convertImage(foo[foo.length - 1]);

    }
}
