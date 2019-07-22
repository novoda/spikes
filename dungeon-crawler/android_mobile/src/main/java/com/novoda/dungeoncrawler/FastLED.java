package com.novoda.dungeoncrawler;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class FastLED implements Display {

    private final int numberOfLeds;
    private final int ledColorOrder;
    private final int dataPin;
    private final int clockPin;
    private final List<Integer> state;

    FastLED(int numberOfLeds, int ledColorOrder, int dataPin, int clockPin) {
        this.numberOfLeds = numberOfLeds;
        this.ledColorOrder = ledColorOrder;
        this.dataPin = dataPin;
        this.clockPin = clockPin;
        this.state = new ArrayList<>(numberOfLeds);
        for (int i = 0; i < numberOfLeds; i++) {
            state.add(0);
        }
    }

    void setBrightness(int brightness) {
    }

    void setDither(int dither) {
    }

    @Override
    public void clear() {
        state.clear();
    }

    @Override
    public void show() {
        for (Integer integer : state) {
            String led = integer == 0 ? " " : "x";
            Log.d("TUT", "|" + led + "|");
        }
    }

    @Override
    public void set(int position, CRGB rgb) {
        state.add(position, Color.argb(0, rgb.red, rgb.blue, rgb.green));
        // TODO
    }

    @Override
    public void set(int position, CHSV hsv) {
        state.add(position, Color.HSVToColor(0, new float[]{hsv.hue, hsv.sat, hsv.val}));
        // TODO
    }

    // https://github.com/FastLED/FastLED/blob/03d12093a92ee2b64fabb03412aa0c3e4f6384dd/pixeltypes.h#L196
    @Override
    public void modifyHSV(int position, int hue, int saturation, int value) {
        // TODO
    }

    @Override
    public void modifyScale(int position, int scale) {
//        LEDS[i].nscale8(250);
        // TODO
    }

    @Override
    public void modifyMod(int position, int mod) {
//        LEDS[i].mod(100);
        // TODO
    }

}
