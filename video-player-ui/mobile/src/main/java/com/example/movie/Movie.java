package com.example.movie;

import android.graphics.Color;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Movie {

    private static final long MOVIE_LENGTH_MILLIS = TimeUnit.MINUTES.toMillis(1);
    private static final int COLOR_HUE = 0;
    private static final float COLOR_SATURATION = 0.4f;
    private static final float COLOR_VALUE = 0.85f;
    private static final String TEXT_FORMAT = "%.2f!";

    private final float[] hsvColor = new float[]{COLOR_HUE, COLOR_SATURATION, COLOR_VALUE};

    public long durationMillis() {
        return MOVIE_LENGTH_MILLIS;
    }

    public String text(long currentPositionMillis) {
        return String.format(Locale.US, TEXT_FORMAT, 0.001f * currentPositionMillis);
    }

    public int color(long currentPositionMillis) {
        hsvColor[0] = 360f * currentPositionMillis / durationMillis();
        return Color.HSVToColor(hsvColor);
    }

}
