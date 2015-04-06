package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;

class State {

    private ViewPager.OnPageChangeListener delegateOnPageListener;
    private Coordinates indicatorCooridinates;

    void updateDelegateOnPageListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.delegateOnPageListener = onPageChangeListener;
    }

    void updateIndicatorCoordinates(Coordinates indicatorCooridinates) {
        this.indicatorCooridinates = indicatorCooridinates;
    }

    ViewPager.OnPageChangeListener getDelegateOnPageListener() {
        return delegateOnPageListener;
    }

    public Coordinates getIndicatorCoordinates() {
        return indicatorCooridinates;
    }
}
