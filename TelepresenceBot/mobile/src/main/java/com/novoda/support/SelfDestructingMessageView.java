package com.novoda.support;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * A {@link TextView} that clears its content after a set period of time
 */
public class SelfDestructingMessageView extends androidx.appcompat.widget.AppCompatTextView {

    private static final long COMMAND_FADING_DELAY = TimeUnit.MILLISECONDS.toMillis(100);

    public SelfDestructingMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showPermanently(String arrowCharacter) {
        clearMessage();
        setText(arrowCharacter);
    }

    public void showTimed(String string) {
        clearMessage();
        setText(string);
        getHandler().postDelayed(clearTextRunnable, COMMAND_FADING_DELAY);
    }

    private void clearMessage() {
        setText(null);
        if (getHandler() != null) {
            getHandler().removeCallbacks(clearTextRunnable);
        }
    }

    private final Runnable clearTextRunnable = new Runnable() {
        @Override
        public void run() {
            SelfDestructingMessageView.super.setText(null);
        }
    };

}
