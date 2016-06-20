package com.novoda.bonfire.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.novoda.bonfire.R;

public class BubblyBackgroundLayout extends LinearLayout {

    private final Paint paint;
    private final RectF rect;
    private final int bumpDiameter;
    private final int smallBubbleDiameter;
    private final int smallBubbleDistance;
    private final int bubbleCurveHeight;

    public BubblyBackgroundLayout(Context context) {
        this(context, null);
    }

    public BubblyBackgroundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubblyBackgroundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        rect = new RectF();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubblyBackgroundLayout, 0, 0);
        try {
            bubbleCurveHeight = getPixelSizeOrDefault(ta, R.styleable.BubblyBackgroundLayout_bubbleBigCurveDiameter, R.dimen.bubble_big_height);
            bumpDiameter = getPixelSizeOrDefault(ta, R.styleable.BubblyBackgroundLayout_bubbleBumpDiameter, R.dimen.bubble_bump_diameter);
            smallBubbleDiameter = getPixelSizeOrDefault(ta, R.styleable.BubblyBackgroundLayout_smallBubbleDiameter, R.dimen.bubble_small_diameter);
            smallBubbleDistance = getPixelSizeOrDefault(ta, R.styleable.BubblyBackgroundLayout_smallBubbleDistance, R.dimen.bubble_distance);
            paint.setColor(ta.getColor(R.styleable.BubblyBackgroundLayout_bubbleColor, getResources().getColor(R.color.white)));
        } finally {
            ta.recycle();
        }
    }

    private int getPixelSizeOrDefault(TypedArray ta, int index, int defaultResIndex) {
        return ta.getDimensionPixelSize(index, getResources().getDimensionPixelSize(defaultResIndex));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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

}
