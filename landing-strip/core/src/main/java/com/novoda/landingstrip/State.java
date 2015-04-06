package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;

class State {

    private float offset;
    private int position;
    private ViewPager.OnPageChangeListener delegateOnPageListener;
    private Coordinates indicatorCooridinates;

    void updateOffset(float offset) {
        this.offset = offset;
    }

    void updatePosition(int position) {
        this.position = position;
    }

    void updateDelegateOnPageListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.delegateOnPageListener = onPageChangeListener;
    }

    void updateIndicatorCoordinates(Coordinates indicatorCooridinates) {
        this.indicatorCooridinates = indicatorCooridinates;
    }

    float getOffset() {
        return offset;
    }

    int getPosition() {
        return position;
    }

    ViewPager.OnPageChangeListener getDelegateOnPageListener() {
        return delegateOnPageListener;
    }

    public Coordinates getIndicatorCoordinates() {
        return indicatorCooridinates;
    }
}
