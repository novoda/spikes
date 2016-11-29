package com.novoda.tpbot.support;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.novoda.tpbot.R;

public class TextViewWithForeground extends TextView {

    private static final int INVALID_RESOURCE_ID = 0;
    private Drawable foreground;

    public TextViewWithForeground(Context context, AttributeSet attrs) {
        super(context, attrs);
        extractForegroundFrom(context, attrs);
    }

    private void extractForegroundFrom(Context context, AttributeSet attrs) {
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithForeground);

        if (styledAttributes == null) {
            return;
        }

        try {
            int resourceId = styledAttributes.getResourceId(R.styleable.TextViewWithForeground_foreground, INVALID_RESOURCE_ID);

            if (resourceId == INVALID_RESOURCE_ID) {
                return;
            }
            Drawable drawable = getResources().getDrawable(resourceId);
            updateForegroundWith(drawable);

        } finally {
            styledAttributes.recycle();
        }
    }

    private void updateForegroundWith(Drawable foreground) {
        this.foreground = foreground;
        if (foreground == null) {
            setWillNotDraw(true);
        } else {
            setWillNotDraw(false);
            foreground.setCallback(this);
            if (foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who.equals(foreground);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foreground != null) {
            foreground.jumpToCurrentState();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (foreground != null && foreground.isStateful()) {
            foreground.setState(getDrawableState());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (foreground != null) {
            foreground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (foreground != null) {
            foreground.draw(canvas);
        }
    }

}
