package com.novoda.bonfire.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.novoda.bonfire.R;

public class BubblyLinearLayout extends LinearLayout {

    private final Paint paint;
    private final RectF rect;
    private final int bumpDiameter;
    private final int smallBubbleDiameter;
    private final int smallBubbleDistance;
    private final int bubbleCurveHeight;

    public BubblyLinearLayout(Context context) {
        this(context, null);
    }

    public BubblyLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubblyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        rect = new RectF();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubblyLinearLayout, 0, 0);
        try {
            bubbleCurveHeight = ta.getDimensionPixelSize(R.styleable.BubblyLinearLayout_bubbleBigCurveHeight, 0);
            bumpDiameter = ta.getDimensionPixelSize(R.styleable.BubblyLinearLayout_bubbleBumpDiameter, 0);
            smallBubbleDiameter = ta.getDimensionPixelSize(R.styleable.BubblyLinearLayout_smallBubbleDiameter, 0);
            smallBubbleDistance = ta.getDimensionPixelSize(R.styleable.BubblyLinearLayout_smallBubbleDistance, 0);
            paint.setColor(ta.getColor(R.styleable.BubblyLinearLayout_bubbleColor, 0));
        } finally {
            ta.recycle();
        }
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
