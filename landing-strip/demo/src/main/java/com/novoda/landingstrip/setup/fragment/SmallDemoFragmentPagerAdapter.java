package com.novoda.landingstrip.setup.fragment;

import android.support.v4.app.FragmentManager;

public class SmallDemoFragmentPagerAdapter extends DemoFragmentPagerAdapter {
    private static final int ITEM_COUNT = 3;

    public SmallDemoFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}
