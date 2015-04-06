package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;

class State {

    private ViewPager.OnPageChangeListener delegateOnPageListener;
    private float positionOffset;
    private int position;

    void updateDelegateOnPageListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.delegateOnPageListener = onPageChangeListener;
    }

    ViewPager.OnPageChangeListener getDelegateOnPageListener() {
        return delegateOnPageListener;
    }

    public void updatePositionOffset(float positionOffset) {
        this.positionOffset = positionOffset;
    }

    public void updatePosition(int position) {
        this.position = position;
    }

    public float getPositionOffset() {
        return positionOffset;
    }

    public int getPosition() {
        return position;
    }

}
