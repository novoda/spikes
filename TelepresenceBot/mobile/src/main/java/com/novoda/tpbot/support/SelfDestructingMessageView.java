package com.novoda.tpbot.support;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A {@link TextView} that clears its content after a set period of time
 */
public class SelfDestructingMessageView extends TextView {

    public SelfDestructingMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showPermanently(String arrowCharacter) {
        setText(arrowCharacter);
        getHandler().removeCallbacks(clearTextRunnable);
    }

    public void showTimed(String string, long timeInMillis) {
        setText(string);
        getHandler().removeCallbacks(clearTextRunnable);
        getHandler().postDelayed(clearTextRunnable, timeInMillis);
    }

    public void clearMessage() {
        setText(null);
        getHandler().removeCallbacks(clearTextRunnable);
    }

    private final Runnable clearTextRunnable = new Runnable() {
        @Override
        public void run() {
            SelfDestructingMessageView.super.setText(null);
        }
    };

}
