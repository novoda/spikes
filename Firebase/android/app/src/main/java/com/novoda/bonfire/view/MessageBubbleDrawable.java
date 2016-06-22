package com.novoda.bonfire.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;

import com.novoda.bonfire.R;

public class MessageBubbleDrawable extends Drawable {

    public enum Gravity {
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

    public MessageBubbleDrawable(Context context, @ColorRes int color, Gravity gravity) {
        this.gravity = gravity;
        rect = new RectF();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(color));

        resolveDimensions(context.getResources());
    }

    private void resolveDimensions(Resources resources) {
        messageBubblePadding = resources.getDimensionPixelSize(R.dimen.chat_message_bubble_left_margin);
        messageBubbleCornerRadius = resources.getDimensionPixelSize(R.dimen.chat_message_bubble_corner_radius);
        bubbleCenterVertical = resources.getDimensionPixelSize(R.dimen.chat_message_bump_vertical_position);
        bumpDiameter = resources.getDimensionPixelSize(R.dimen.chat_message_bump_diameter);
        smallBubbleDiameter = resources.getDimensionPixelSize(R.dimen.chat_message_small_bubble_diameter);
    }

    @Override
    public void draw(Canvas canvas) {
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
