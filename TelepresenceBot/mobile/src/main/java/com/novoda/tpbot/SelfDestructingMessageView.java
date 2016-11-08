package com.novoda.tpbot;

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
        super.setText(arrowCharacter);
        getHandler().removeCallbacks(clearTextRunnable);
    }

    public void showTimed(String string, long timeInMillis) {
        super.setText(string);
        getHandler().removeCallbacks(clearTextRunnable);
        getHandler().postDelayed(clearTextRunnable, timeInMillis);
    }

    public void clearMessage() {
        getHandler().removeCallbacks(clearTextRunnable);
    }

    private final Runnable clearTextRunnable = new Runnable() {
        @Override
        public void run() {
            SelfDestructingMessageView.super.setText(null);
        }
    };

}
