package com.reacttwitter.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

public class ReactButton extends Button {

    public ReactButton(Context context) {
        super(context);
    }

    public ReactButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReactButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReactButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
