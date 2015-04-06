package com.novoda.landingstrip.setup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DemoPagerAdapter extends FragmentPagerAdapter {

    public DemoPagerAdapter(FragmentManager fm) {
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
