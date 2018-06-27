package com.novoda.dungeoncrawler;

public interface Display {
    void clear();

    void show();

    void set(int position, CRGB rgb);

    void set(int position, CHSV hsv);

    // https://github.com/FastLED/FastLED/blob/03d12093a92ee2b64fabb03412aa0c3e4f6384dd/pixeltypes.h#L196
    void modifyHSV(int position, int hue, int saturation, int value);

    void modifyScale(int position, int scale);

    void modifyMod(int position, int mod);

    class CRGB {

        static final CRGB DARK_RED = new CRGB(200, 10, 50);
        static final CRGB RED = new CRGB(255, 0, 0);
        static final CRGB GREEN = new CRGB(0, 255, 0);
        static final CRGB WHITE = new CRGB(255, 255, 255);

        final int red;
        final int green;
        final int blue;

        CRGB(int r, int g, int b) {
            this.red = r;
            this.green = g;
            this.blue = b;
        }
    }

    class CHSV {
        final int hue;
        final int sat;
        final int val;

        public CHSV(int hue, int sat, int val) {
            this.hue = hue;
            this.sat = sat;
            this.val = val;
        }
    }
}
