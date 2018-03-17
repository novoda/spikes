package com.novoda.dungeoncrawler;

import android.util.Log;

class FastLED {

    private final int numberOfLeds;
    private final int ledColorOrder;
    private final int dataPin;
    private final int clockPin;

    FastLED(int numberOfLeds, int ledColorOrder, int dataPin, int clockPin) {
        this.numberOfLeds = numberOfLeds;
        this.ledColorOrder = ledColorOrder;
        this.dataPin = dataPin;
        this.clockPin = clockPin;
    }

    void setBrightness(int brightness) {
    }

    void setDither(int dither) {
    }

    void clear() {
        Log.d("TUT", "CLEAR");
    }

    void show() {
        Log.d("TUT", "SHOW");
    }

    void set(int position, CRGB rgb) {
        // TODO
    }

    void set(int position, CHSV hsv) {
        // TODO
    }

    // https://github.com/FastLED/FastLED/blob/03d12093a92ee2b64fabb03412aa0c3e4f6384dd/pixeltypes.h#L196
    public void modifyHSV(int position, int hue, int saturation, int value) {
        // TODO
    }

    public void modifyScale(int position, int scale) {
//        LEDS[i].nscale8(250);
        // TODO
    }

    public void modifyMod(int position, int mod) {
//        LEDS[i].mod(100);
        // TODO
    }

    static class CRGB {

        static final CRGB DARK_RED = new CRGB(200, 10, 50);
        static final CRGB RED = new CRGB(255, 0, 0);
        static final CRGB GREEN = new CRGB(0, 255, 0);
        static final CRGB WHITE = new CRGB(255, 255, 255);

        private final int red;
        private final int green;
        private final int blue;

        CRGB(int r, int g, int b) {
            this.red = r;
            this.green = g;
            this.blue = b;
        }
    }
}
