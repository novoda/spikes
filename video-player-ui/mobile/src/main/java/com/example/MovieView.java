package com.example;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.movie.MoviePlayer;

public class MovieView extends View {

    private final Paint textPaint = createTextPaint();
    private final Paint backgroundPaint = createBackgroundFillPaint();

    private String text;

    public MovieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private Paint createTextPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        return paint;
    }

    private Paint createBackgroundFillPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public void bind(String text, @ColorInt int backgroundColor) {
        this.text = text;
        this.backgroundPaint.setColor(backgroundColor);

        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawPaint(backgroundPaint);

        float textWidth = textPaint.measureText(text);
        canvas.drawText(text, canvas.getWidth() * 0.5f - textWidth * 0.5f, canvas.getHeight() * 0.5f - 50, textPaint);
    }

}
