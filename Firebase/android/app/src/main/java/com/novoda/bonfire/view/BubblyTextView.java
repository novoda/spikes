package com.novoda.bonfire.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.novoda.bonfire.R;

public class BubblyTextView extends TextView {

    private enum Gravity {
        START, END
    }

    private final Paint paint;
    private final RectF rect;
    private Gravity gravity;
    private int messageBubblePadding;
    private int messageBubbleCornerRadius;
    private int bubbleCenterVertical;
    private int bumpDiameter;
    private int smallBubbleDiameter;

    public BubblyTextView(Context context) {
        this(context, null);
    }

    public BubblyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubblyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rect = new RectF();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        resolveAttributes(context, attrs);
        resolveDimensions(getResources());
    }

    private void resolveAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubblyTextView, 0, 0);
        try {
            paint.setColor(typedArray.getColor(R.styleable.BubblyTextView_bubbleColor, getResources().getColor(R.color.bubble_grey)));
            gravity = Gravity.values()[typedArray.getInt(R.styleable.BubblyTextView_bubbleGravity, 0)];
        } finally {
            typedArray.recycle();
        }
    }

    private void resolveDimensions(Resources resources) {
        messageBubblePadding = resources.getDimensionPixelSize(R.dimen.chat_message_bubble_left_margin);
        messageBubbleCornerRadius = resources.getDimensionPixelSize(R.dimen.chat_message_bubble_corner_radius);
        bubbleCenterVertical = resources.getDimensionPixelSize(R.dimen.chat_message_bump_vertical_position);
        bumpDiameter = resources.getDimensionPixelSize(R.dimen.chat_message_bump_diameter);
        smallBubbleDiameter = resources.getDimensionPixelSize(R.dimen.chat_message_small_bubble_diameter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        super.onDraw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        int messageBubbleLeft = gravity == Gravity.START ? messageBubblePadding : 0;
        int messageBubbleRight = gravity == Gravity.START ? canvas.getWidth() : canvas.getWidth() - messageBubblePadding;
        int bumpCenterHorizontal = gravity == Gravity.START ? messageBubbleLeft : messageBubbleRight;
        int smallBubbleCenterHorizontal = gravity == Gravity.START ? smallBubbleDiameter / 2 : canvas.getWidth() - smallBubbleDiameter / 2;

        rect.set(messageBubbleLeft, 0, messageBubbleRight, canvas.getHeight());
        canvas.drawRoundRect(rect, messageBubbleCornerRadius, messageBubbleCornerRadius, paint);
        drawBubble(canvas, bumpCenterHorizontal, bubbleCenterVertical, bumpDiameter);
        drawBubble(canvas, smallBubbleCenterHorizontal, bubbleCenterVertical, smallBubbleDiameter);
    }

    private void drawBubble(Canvas canvas, int xPosition, int yPosition, int diameter) {
        int radius = diameter / 2;
        rect.set(xPosition - radius, yPosition - radius, xPosition + radius, yPosition + radius);
        canvas.drawRoundRect(rect, radius, radius, paint);
    }
}
