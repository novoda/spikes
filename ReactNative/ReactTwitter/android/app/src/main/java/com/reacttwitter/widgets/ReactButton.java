package com.reacttwitter.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

class ReactButton extends Button {

    public ReactButton(Context context) {
        super(context);
        init();
    }

    public ReactButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReactButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReactButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setAllCaps(false);
    }

    public void setBackgroundImage(String backgroundImage) {
        setBackground(getDrawableByName(backgroundImage));
    }

    private Drawable getDrawableByName(String name) {
        int resId = getResources().getIdentifier(name.toLowerCase(), "drawable", getContext().getPackageName());
        if (resId <= 0) {
            return null;
        }

        return ContextCompat.getDrawable(getContext(), resId);
    }
}
