package com.novoda.landingstrip.setup.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.novoda.landingstrip.setup.SmallData;

public class DemoFragmentSmallPagerAdapter extends FragmentPagerAdapter {

    public DemoFragmentSmallPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return SmallData.values()[position].getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return SmallViewPagerFragment.newInstance(SmallData.values()[position]);
    }

    @Override
    public int getCount() {
        return SmallData.values().length;
    }
}
