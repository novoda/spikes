package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;

class State {

    private ViewPager.OnPageChangeListener delegateOnPageListener;
    private float pagePositionOffset;
    private int position;

    private int fastForwardPosition;

    void updateDelegateOnPageListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.delegateOnPageListener = onPageChangeListener;
    }

    ViewPager.OnPageChangeListener getDelegateOnPageListener() {
        return delegateOnPageListener;
    }

    public void updatePositionOffset(float positionOffset) {
        this.pagePositionOffset = positionOffset;
    }

    public void updatePosition(int position) {
        this.position = position;
    }

    public float getPagePositionOffset() {
        return pagePositionOffset;
    }

    public int getPosition() {
        return position;
    }

    public void updateFastForwardPosition(int fastForwardPosition) {
        this.fastForwardPosition = fastForwardPosition;
    }

    public int getFastForwardPosition() {
        return fastForwardPosition;
    }

    public void invalidateFastForwardPosition() {
        updateFastForwardPosition(-1);
    }

    public boolean fastForwardPositionIsValid() {
        return getFastForwardPosition() != -1;
    }
}
