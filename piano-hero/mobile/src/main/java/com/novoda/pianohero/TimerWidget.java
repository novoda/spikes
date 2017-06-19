package com.novoda.pianohero;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import flepsik.github.com.progress_ring.ProgressRingView;

public class TimerWidget extends FrameLayout {

    private ProgressRingView progressRingView;
    private TextView textView;

    public TimerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_timer_widget, this);
        textView = ((TextView) findViewById(R.id.timer_widget_text));
        progressRingView = ((ProgressRingView) findViewById(R.id.timer_widget_progress_ring));
    }

    void setProgress(@FloatRange(from = 0f, to = 1f) float newProgress) {
        progressRingView.setProgress(newProgress);
    }

    void setText(CharSequence text) {
        textView.setText(text);
    }
}
