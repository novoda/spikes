package com.novoda.landingstrip.setup.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.novoda.landingstrip.setup.Data;

public class DemoFragmentPagerAdapter extends FragmentPagerAdapter {

    public DemoFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Data.values()[position].getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPagerFragment.newInstance(Data.values()[position]);
    }

    @Override
    public int getCount() {
        return Data.values().length;
    }
}
