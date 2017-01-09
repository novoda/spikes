package com.novoda.tpbot.support;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * A {@link TextView} that clears its content after a set period of time
 */
public class SelfDestructingMessageView extends TextView {

    private static final long COMMAND_FADING_DELAY = TimeUnit.MILLISECONDS.toMillis(100);

    public SelfDestructingMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showPermanently(String arrowCharacter) {
        setText(arrowCharacter);
        getHandler().removeCallbacks(clearTextRunnable);
    }

    public void showTimed(String string) {
        setText(string);
        getHandler().removeCallbacks(clearTextRunnable);
        getHandler().postDelayed(clearTextRunnable, COMMAND_FADING_DELAY);
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
