package com.novoda.viewtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RelativeLayoutWithForeground extends RelativeLayout {

    private static final int INVALID_FOREGROUND_ID = 0;

    private Drawable foreground;

    public RelativeLayoutWithForeground(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyForeground(context, attrs);
    }

    public RelativeLayoutWithForeground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyForeground(context, attrs);
    }

    private void applyForeground(Context context, AttributeSet attrs) {
        TypedArray xml = context.obtainStyledAttributes(attrs, R.styleable.RelativeLayoutWithForeground);
        int foregroundResId = INVALID_FOREGROUND_ID;
        try {
            foregroundResId = xml.getResourceId(R.styleable.RelativeLayoutWithForeground_foreground, INVALID_FOREGROUND_ID);
        } finally {
            xml.recycle();
        }


        if (foregroundResId == INVALID_FOREGROUND_ID) {
            return;
        }
        Drawable drawable = getDrawable(getResources(), foregroundResId);
        setForeground(drawable);
    }

    @SuppressWarnings("deprecation") // getDrawable(int) deprecated since API 21, but the replacement is not available til then..!
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static Drawable getDrawable(Resources resources, int foregroundResId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(foregroundResId);
        } else {
            return resources.getDrawable(foregroundResId, null);
        }
    }

    public Drawable getForeground() {
        return foreground;
    }

    public void setForeground(Drawable foreground) {
        if (this.foreground == foreground) {
            return;
        }
        removeOldForegroundIfAny();
        updateForeground(foreground);
    }

    private void updateForeground(Drawable foreground) {
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

    private void removeOldForegroundIfAny() {
        if (foreground != null) {
            foreground.setCallback(null);
            unscheduleDrawable(foreground);
        }
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals") // Matching super implementation
    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (who == foreground);
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
