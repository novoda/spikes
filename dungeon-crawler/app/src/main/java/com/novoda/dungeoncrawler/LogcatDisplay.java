package com.novoda.dungeoncrawler;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class LogcatDisplay implements Display {

    private final List<Integer> state;

    LogcatDisplay(int numberOfLeds) {
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
        int numberOfLeds = state.size();
        state.clear();
        for (int i = 0; i < numberOfLeds; i++) {
            state.add(0);
        }
    }

    @Override
    public void show() {
        Log.d("TUT", "SHOW");
        for (Integer integer : state) {
            String led = integer == 0 ? " " : "x";
            Log.d("TUT", "|" + led + "|");
        }
    }

    @Override
    public void set(int position, CRGB rgb) {
        state.add(position, Color.argb(0, rgb.red, rgb.blue, rgb.green));
    }

    @Override
    public void set(int position, CHSV hsv) {
        state.add(position, Color.HSVToColor(0, new float[]{hsv.hue, hsv.sat, hsv.val}));
    }

    @Override
    public void modifyHSV(int position, int hue, int saturation, int value) {
        // TODO
    }

    @Override
    public void modifyScale(int position, int scale) {
        // TODO
    }

    @Override
    public void modifyMod(int position, int mod) {
        // TODO
    }

}
