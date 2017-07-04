package com.novoda.pianohero.hax;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class HaxActivity extends AppCompatActivity {

    private Handler handler;
    private RGBmatrixPanel rgbMatrixPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");

        HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();
        handler = new Handler(thread.getLooper());

        rgbMatrixPanel = new RGBmatrixPanel(new GpioProxy());
        rgbMatrixPanel.clearDisplay();
        handler.post(hax);

//        rgbMatrixPanel.drawPixel(3, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(4, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(5, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(6, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(7, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(8, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 3, Color.YELLOW);
//
//        rgbMatrixPanel.drawPixel(3, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(3, 4, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(3, 5, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(3, 6, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(3, 7, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(3, 8, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(3, 9, Color.YELLOW);
//
//        rgbMatrixPanel.drawPixel(9, 3, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 4, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 5, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 6, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 7, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 8, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 9, Color.YELLOW);
//
//        rgbMatrixPanel.drawPixel(3, 9, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(4, 9, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(5, 9, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(6, 9, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(7, 9, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(8, 9, Color.YELLOW);
//        rgbMatrixPanel.drawPixel(9, 9, Color.YELLOW);

        rgbMatrixPanel.setFontColor(Color.YELLOW);
        rgbMatrixPanel.writeText("Hello?");
    }

    private final Runnable hax = new Runnable() {
        @Override
        public void run() {
            rgbMatrixPanel.updateDisplay();
            handler.post(this);
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(hax);
        super.onDestroy();
    }
}

