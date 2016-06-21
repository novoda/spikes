package com.novoda.bonfire.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.novoda.bonfire.R;

public class BubblyDrawable extends Drawable{

    private final Paint paint;
    private final RectF rect;
    private final int bumpDiameter;
    private final int smallBubbleDiameter;
    private final int smallBubbleDistance;
    private final int bubbleCurveHeight;

    public BubblyDrawable(Resources resources) {
        this.bumpDiameter = resources.getDimensionPixelSize(R.dimen.bubble_bump_diameter);
        this.smallBubbleDiameter = resources.getDimensionPixelSize(R.dimen.small_bubble_diameter);
        this.smallBubbleDistance = resources.getDimensionPixelSize(R.dimen.small_bubble_distance);
        this.bubbleCurveHeight = resources.getDimensionPixelSize(R.dimen.big_bubble_curved_height);
        this.rect = new RectF();
        this.paint = new Paint();
        paint.setColor(resources.getColor(R.color.white));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();

        drawBigBubble(canvas, canvasWidth, canvasHeight, bubbleCurveHeight);
        int horizontalCenter = canvasWidth / 2;
        int bumpCenter = canvasHeight - bubbleCurveHeight;
        drawBubble(canvas, horizontalCenter, bumpCenter, bumpDiameter);

        int smallBubbleCenter = bumpCenter + bumpDiameter + smallBubbleDistance;
        drawBubble(canvas, horizontalCenter, smallBubbleCenter, smallBubbleDiameter);
    }

    private void drawBigBubble(Canvas canvas, int canvasWidth, int canvasHeight, int bottomPadding) {
        int bottomPos = canvasHeight - bottomPadding * 2;
        canvas.drawRect(0, 0, canvasWidth, bottomPos, paint);
        rect.set(0, bottomPos - bottomPadding, canvasWidth, canvasHeight - bottomPadding);
        canvas.drawArc(rect, 0, 180, false, paint);
    }

    private void drawBubble(Canvas canvas, int xPosition, int yPosition, int diameter) {
        int radius = diameter / 2;
        rect.set(xPosition - radius, yPosition - radius, xPosition + radius, yPosition + radius);
        canvas.drawRoundRect(rect, radius, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
