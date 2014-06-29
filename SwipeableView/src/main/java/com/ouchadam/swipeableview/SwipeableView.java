package com.ouchadam.swipeableview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SwipeableView extends RelativeLayout implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener {

    private ViewGroup firstPage;

    public SwipeableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        validateUsage();
    }

    private void validateUsage() {
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new RuntimeException("Can only have two children at the moment");
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        firstPage = (ViewGroup) getChildAt(1);
        setOnClickListener(this);
    }

    public void slide() {
        Toast.makeText(getContext(), "Sliding", Toast.LENGTH_SHORT).show();
        ValueAnimator va = ValueAnimator.ofInt(getWidth() / 2, 0);
        va.setDuration(500);
        va.setStartDelay(0L);
        va.addUpdateListener(this);
        va.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec * 2, heightMeasureSpec);
        int wspec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 2, MeasureSpec.EXACTLY);
        int hspec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(wspec, hspec);
        }
    }

    @Override
    public void onClick(View view) {
        slide();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        Integer value = (Integer) valueAnimator.getAnimatedValue();
        int i = value.intValue();
        firstPage.getLayoutParams().width = i;
        requestLayout();
    }
}
