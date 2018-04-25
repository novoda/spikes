package com.novoda.dungeoncrawler;

public class Ws2801Display { // implements Display {
//
//    private static final String LOG_TAG = Ws2801Display.class.getSimpleName();
//    private static final int OPAQUE = 255;
//
//    private final Ws2801 ws2801;
//    private final int[] leds;
//
//    public Ws2801Display(Ws2801 ws2801, int numberOfLeds) {
//        this.ws2801 = ws2801;
//        this.leds = new int[numberOfLeds];
//        Arrays.fill(leds, 0);
//    }
//
//    @Override
//    public void show() {
//        try {
//            ws2801.write(leds);
//        } catch (IOException e) {
//            Log.w(LOG_TAG, "Unable to write values to Ws2801 driver", e);
//        }
//    }
//
//    @Override
//    public void clear() {
//        Arrays.fill(leds, 0);
//    }
////
//    @Override
//    public void set(int position, CRGB rgb) {
//        leds[position] = Color.argb(OPAQUE, rgb.red, rgb.green, rgb.blue);
//    }
//
//    @Override
//    public void set(int position, CHSV hsv) {
//        leds[position] = Color.HSVToColor(OPAQUE, new float[]{hsv.hue, hsv.sat, hsv.val});
//    }
////
//    @Override
//    public void modifyHSV(int position, int hue, int saturation, int value) {
//        leds[position] = Color.HSVToColor(OPAQUE, new float[]{hue, saturation, value});
//    }
////
//    @Override
//    public void modifyScale(int position, int scale) {
//        transform(position, colourComponent -> (int) colourComponent); // TODO: Apply scale
//    }
////
//    @Override
//    public void modifyMod(int position, int mod) {
//        transform(position, colourComponent -> (int) colourComponent % mod); // TODO is this correct
//    }
//
//    private void transform(int position, Transformation transformation) {
//        Color color = Color.valueOf(leds[position]);
//        int scaled = Color.argb(OPAQUE, transformation.apply(color.red()), transformation.apply(color.green()), transformation.apply(color.blue()));
//        leds[position] = scaled;
//    }
//
//    private interface Transformation {
//        int apply(float colourComponent);
//    }

}
