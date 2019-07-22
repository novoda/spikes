package com.novoda.dungeoncrawler;

import android.graphics.Color;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;

import java.io.IOException;
import java.util.Arrays;

public class Apa102Display implements Display {

    private static final String LOG_TAG = Apa102Display.class.getSimpleName();
    private static final int LED_BRIGHTNESS = 5; // 0 ... 31

    private static final int OPAQUE = 255;

    private final Apa102 apa102;
    private final int[] leds;

    public Apa102Display(Apa102 apa102, int numberOfLeds) {
        this.apa102 = apa102;
        this.leds = new int[numberOfLeds];
        Arrays.fill(leds, 0);

        this.apa102.setBrightness(LED_BRIGHTNESS);
    }

    @Override
    public void show() {
        try {
            apa102.write(leds);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Unable to write values to Ws2801 driver", e);
        }
    }

    @Override
    public void clear() {
        Arrays.fill(leds, 0);
    }

    //
    @Override
    public void set(int position, CRGB rgb) {
        leds[position] = Color.argb(OPAQUE, rgb.red, rgb.green, rgb.blue);
    }

    @Override
    public void set(int position, CHSV hsv) {
        leds[position] = Color.HSVToColor(OPAQUE, new float[]{hsv.hue, hsv.sat, hsv.val});
    }

    //
    @Override
    public void modifyHSV(int position, int hue, int saturation, int value) {
        leds[position] = Color.HSVToColor(OPAQUE, new float[]{hue, saturation, value});
    }

    //
    @Override
    public void modifyScale(int position, int scale) {
        transform(position, colourComponent -> (int) colourComponent * scale); // TODO is this correct?
    }

    //
    @Override
    public void modifyMod(int position, int mod) {
        transform(position, colourComponent -> (int) colourComponent % mod); // TODO is this correct?
    }

    private void transform(int position, Transformation transformation) {
        Color color = Color.valueOf(leds[position]);
        int transformed = Color.argb(OPAQUE, transformation.apply(color.red()), transformation.apply(color.green()), transformation.apply(color.blue()));
        leds[position] = transformed;
    }

    private interface Transformation {
        int apply(float colourComponent);
    }

}
