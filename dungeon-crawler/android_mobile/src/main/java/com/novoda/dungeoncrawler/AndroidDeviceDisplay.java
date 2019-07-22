package com.novoda.dungeoncrawler;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

class AndroidDeviceDisplay implements Display {

    private static final int OPAQUE = 255;
    private static final int LED_SIZE = 24;
    private final List<Integer> state;
    private final LinearLayout linearLayout;
    private final Handler handler;

    AndroidDeviceDisplay(Context context, ViewGroup ledsContainer, int numberOfLeds) {
        this.state = new ArrayList<>(numberOfLeds);
        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < numberOfLeds; i++) {
            View ledView = new AndroidLedView(context);
            ledView.setLayoutParams(new LinearLayout.LayoutParams(LED_SIZE, LED_SIZE));
            ledView.setId(999 + i);
            linearLayout.addView(ledView, i);
            state.add(i, Color.BLACK);
        }
        ledsContainer.addView(linearLayout);
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public void clear() {
        int numberOfLeds = state.size();
        for (int i = 0; i < numberOfLeds; i++) {
            state.set(i, Color.BLACK);
        }
    }

    @Override
    public void show() {
        List<Integer> current = new ArrayList<>(state.size());
        for (int i = 0; i < state.size(); i++) {
            current.add(i, state.get(i));
        }
        handler.removeCallbacksAndMessages(null);
        handler.post(() -> {
            for (int i = 0; i < current.size(); i++) {
                int integer = current.get(i);
                AndroidLedView ledView = (AndroidLedView) linearLayout.getChildAt(i);
                if (ledView != null) {
                    ledView.setBackgroundColor(integer);
                }
            }
        });
    }

    @Override
    public void set(int position, CRGB rgb) {
        state.set(position, Color.argb(OPAQUE, rgb.red, rgb.green, rgb.blue));
    }

    @Override
    public void set(int position, CHSV hsv) {
        state.set(position, Color.HSVToColor(OPAQUE, new float[]{hsv.hue, hsv.sat, hsv.val}));
    }

    @Override
    public void modifyHSV(int position, int hue, int saturation, int value) {
        // TODO is this correct
        set(position, new CHSV(hue, saturation, value));
    }

    @Override
    public void modifyScale(int position, int scale) {
        transform(position, colourComponent -> (int) colourComponent); // TODO: Apply scale
    }

    private void transform(int position, Transformation transformation) {
        Color color = Color.valueOf(state.get(position));
        int scaled = Color.argb(OPAQUE, transformation.apply(color.red()), transformation.apply(color.green()), transformation.apply(color.blue()));
        state.set(position, scaled);
    }

    @Override
    public void modifyMod(int position, int mod) {
        transform(position, colourComponent -> (int) colourComponent % mod); // TODO is this correct
    }

    private interface Transformation {

        int apply(float colourComponent);
    }

}
