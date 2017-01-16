package com.xavirigau.ledcontroller;

import com.xrigau.driver.ws2801.Ws2801;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class RainbowLedActivity extends Activity {

    private static final String TAG = RainbowLedActivity.class.getSimpleName();
    private static final int FRAME_DELAY_MS = 15;
    private static final int NUM_LEDS = 1;

    private final int[] mLedColors = new int[NUM_LEDS];

    private Ws2801 mLedstrip;
    private HandlerThread mPioThread;
    private Handler mHandler;

    private float hue = 0.0f;
    private float increment = 0.002f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPioThread = new HandlerThread("pioThread");
        mPioThread.start();
        mHandler = new Handler(mPioThread.getLooper());

        try {
            Log.d(TAG, "Initializing LED strip");
            mLedstrip = Ws2801.create(BoardDefaults.getSPIPort(), Ws2801.Mode.RBG);
            mHandler.post(mAnimateRunnable);
//            mLedstrip.write(new int[]{Color.BLACK}); // Uncomment this line and comment the previous one to turn off
        } catch (IOException e) {
            Log.e(TAG, "Error initializing LED strip", e);
        }
    }

    private Runnable mAnimateRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Arrays.fill(mLedColors, hue2Rgb()); // all LEDs will have the same color
                mLedstrip.write(mLedColors);

                hue += increment;
                if (hue >= 1.0f || hue <= 0.0f) {
                    hue = Math.max(0.0f, Math.min(hue, 1.0f));
                    increment = -increment;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while writing to LED strip", e);
            }
            mHandler.postDelayed(mAnimateRunnable, FRAME_DELAY_MS);
        }
    };

    private int hue2Rgb() {
        float l = 0.5f; // luminosity?
        float s = 0.8f; // saturation?

        float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
        float p = 2f * l - q;

        int r = (int) (convert(p, q, hue + (1 / 3f)) * 255);
        int g = (int) (convert(p, q, hue) * 255);
        int b = (int) (convert(p, q, hue - (1 / 3f)) * 255);

        return Color.rgb(r, g, b);
    }

    private float convert(float p, float q, float t) {
        if (t < 0) t += 1f;
        if (t > 1) t -= 1f;
        if (t < 1 / 6f) return p + (q - p) * 6f * t;
        if (t < 1 / 2f) return q;
        if (t < 2 / 3f) return p + (q - p) * (2 / 3f - t) * 6f;
        return p;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mAnimateRunnable);
        mPioThread.quitSafely();

        try {
            mLedColors[0] = Color.BLACK;
            mLedstrip.write(mLedColors);
            mLedstrip.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception closing LED strip", e);
        }
        super.onDestroy();
    }

}
