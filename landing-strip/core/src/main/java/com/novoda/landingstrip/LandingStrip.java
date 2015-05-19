package com.novoda.landingstrip;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public interface LandingStrip {

    void attach(ViewPager viewPager);

    void attach(ViewPager viewPager, PagerAdapter pagerAdapter);

    void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener);

    interface TabSetterUpper {
        View setUp(int position, CharSequence title, View inflatedTab);
    }
}
