package com.novoda.tpbot.controls;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.novoda.tpbot.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class ForegroundTextView extends androidx.appcompat.widget.AppCompatTextView {

    private static final int INVALID_RESOURCE_ID = 0;
    private Drawable foreground;

    public ForegroundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        extractForegroundFrom(context, attrs);
    }

    private void extractForegroundFrom(Context context, AttributeSet attrs) {
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ForegroundTextView);

        if (styledAttributes == null) {
            return;
        }

        try {
            int resourceId = styledAttributes.getResourceId(R.styleable.ForegroundTextView_foreground, INVALID_RESOURCE_ID);

            if (resourceId == INVALID_RESOURCE_ID) {
                return;
            }
            final Drawable drawable = getDrawableFor(resourceId);
            updateForegroundWith(drawable);

            if (isLollipopOrAfter()) {
                createHotspotTouchListener();
            }

        } finally {
            styledAttributes.recycle();
        }
    }

    private Drawable getDrawableFor(int resourceId) {
        if (isLollipopOrAfter()) {
            return getDrawableLollipop(getResources(), resourceId);
        } else {
            return getDrawablePreLollipop(getResources(), resourceId);
        }
    }

    private static boolean isLollipopOrAfter() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getDrawableLollipop(Resources resources, @DrawableRes int resId) {
        return resources.getDrawable(resId, null);
    }

    private static Drawable getDrawablePreLollipop(Resources resources, @DrawableRes int resId) {
        return resources.getDrawable(resId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createHotspotTouchListener() {
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                foreground.setHotspot(event.getX(), event.getY());
                return false;
            }
        });
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
