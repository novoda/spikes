package com.ouchadam.swipeableview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import javax.xml.transform.sax.TemplatesHandler;

public class SwipeableView extends RelativeLayout implements View.OnClickListener {

    private ViewGroup firstPage;
    private ViewGroup secondPage;

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
        firstPage = (ViewGroup) getChildAt(0);
        secondPage = (ViewGroup) getChildAt(1);
        setOnClickListener(this);
    }

    public void slide() {
        Toast.makeText(getContext(), "Sliding", Toast.LENGTH_SHORT).show();
        Animation slide = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                -1.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                -0.0f);

        slide.setDuration(1000);
        slide.setFillAfter(true);
        firstPage.startAnimation(slide);
        secondPage.startAnimation(slide);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentWidth = r - l;
        View v = getChildAt(0);
        v.layout(0, 0, parentWidth, b - t);

        View v2 = getChildAt(1);
        v2.layout(parentWidth, 0, parentWidth * 2, b - t);
    }

    @Override
    public void onClick(View view) {
        slide();
    }

}
