package com.novoda.inkyphat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
        ImageDrawer imageDrawer = new ImageDrawer();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_1);
        Bitmap[] foo = imageDrawer.filterImage(bitmap);
        for (Bitmap image : foo) {
            ImageView imageview = new ImageView(this);
            imageview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageview.setImageBitmap(image);
            imageview.setBackgroundColor(Color.BLUE);
            imageview.setPadding(20, 20, 20, 20);
            root.addView(imageview);
        }

        InkyPhat.PaletteImage output = imageDrawer.convertImage(foo[foo.length - 1]);

    }
}
