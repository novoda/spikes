package com.ouchadam.swipeableview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SwipeableView extends RelativeLayout implements View.OnClickListener {

    private ViewGroup contentParent;
    private View contentView;

    public SwipeableView(Context context) {
        super(context);
        init();
    }

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
        if (childCount > 1) {
            throw new RuntimeException("Can only have one child at the moment");
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentParent = (ViewGroup) getChildAt(0);
        contentView = contentParent.getChildAt(0);
        setOnClickListener(this);
    }

    public void slide() {
        Toast.makeText(getContext(), "Sliding", Toast.LENGTH_SHORT).show();
        ValueAnimator va = ValueAnimator.ofInt(getWidth() + contentView.getWidth(), contentView.getWidth());
        va.setDuration(1000);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                int i = value.intValue();
                contentParent.getLayoutParams().width = i;
                contentParent.requestLayout();
            }
        });
        va.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + contentView.getWidth(), heightMeasureSpec);
    }

    @Override
    public void onClick(View view) {
        slide();
    }
}
