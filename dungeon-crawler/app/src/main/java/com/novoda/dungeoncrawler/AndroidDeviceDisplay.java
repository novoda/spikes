package com.novoda.dungeoncrawler;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class AndroidDeviceDisplay implements Display {

    private final List<Integer> state;
    private final LinearLayout linearLayout;
    private final Handler handler;

    AndroidDeviceDisplay(Context context, ScrollView scrollView, int numberOfLeds) {
        this.state = new ArrayList<>(numberOfLeds);
        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < numberOfLeds; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setId(999 + i);
            linearLayout.addView(textView, i);
            state.add(0);
        }
        scrollView.addView(linearLayout);
        handler = new Handler(context.getMainLooper());
    }

    void setBrightness(int brightness) {
    }

    void setDither(int dither) {
    }

    @Override
    public void clear() {
        Log.d("TUT", "CLEAR");
        int numberOfLeds = state.size();
        state.clear();
        for (int i = 0; i < numberOfLeds; i++) {
            state.add(0);
        }
    }

    @Override
    public void show() {
        handler.post(() -> {
            for (int i = 0; i < state.size(); i++) {
                int integer = state.get(i);
                String led = integer == 0 ? " " : "x";
                TextView textView = (TextView) linearLayout.getChildAt(i);
                if (textView != null) {
                    textView.setText("|" + led + "|");
                }
            }
        });
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
