package com.novoda.landingstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ExampleRainbowLandingStrip extends LandingStrip {

    private static final int AMPLITUDE = 127;
    private static final int CENTER_FREQUENCY = 128;

    private static final float PHASE_RED = 0f;
    private static final float PHASE_GREEN = 2f;
    private static final float PHASE_BLUE = 4f;

    private final Paint indicatorPaint;

    public ExampleRainbowLandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void drawIndicator(Canvas canvas, Coordinates indicatorCoordinates) {
        float position = indicatorCoordinates.getStart();

        float lastTabOffset = ((ViewGroup) getChildAt(0)).getChildAt(getChildCount() - 1).getWidth();
        float frequency = getOneCycle(lastTabOffset);

        String r = toHex(calculateColour(position, frequency, PHASE_RED, AMPLITUDE, CENTER_FREQUENCY));
        String g = toHex(calculateColour(position, frequency, PHASE_GREEN, AMPLITUDE, CENTER_FREQUENCY));
        String b = toHex(calculateColour(position, frequency, PHASE_BLUE, AMPLITUDE, CENTER_FREQUENCY));

        indicatorPaint.setColor(Color.parseColor("#" + r + g + b));

        int height = getHeight();
        canvas.drawRect(
                indicatorCoordinates.getStart(),
                0,
                indicatorCoordinates.getEnd(),
                height,
                indicatorPaint
        );

    }

    private float getOneCycle(float offset) {
        return (float) (2 * Math.PI / (getChildAt(0).getMeasuredWidth() - offset));
    }

    private int calculateColour(float position, float frequency, float phase, int amplitude, int centerFreq) {
        return (int) (Math.sin((position * frequency) + phase) * amplitude + centerFreq);
    }

    private String toHex(int value) {
        return String.format("%02x", Math.min(value, 255));
    }

}
