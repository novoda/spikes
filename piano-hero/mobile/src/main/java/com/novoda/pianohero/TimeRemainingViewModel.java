package com.novoda.pianohero;

import android.support.annotation.FloatRange;

class TimeRemainingViewModel {

    @FloatRange(from = 0f, to = 1f)
    private final float progress;
    private final CharSequence remainingText;

    TimeRemainingViewModel(@FloatRange(from = 0f, to = 1f) float progress, CharSequence remainingText) {
        this.progress = progress;
        this.remainingText = remainingText;
    }

    public CharSequence getRemainingText() {
        return remainingText;
    }

    @FloatRange(from = 0f, to = 1f)
    public float getProgress() {
        return progress;
    }
}
