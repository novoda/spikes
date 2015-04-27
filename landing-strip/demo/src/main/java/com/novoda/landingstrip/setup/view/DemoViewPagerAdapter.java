package com.novoda.landingstrip.setup.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novoda.landingstrip.setup.Data;

public class DemoViewPagerAdapter extends ViewPagerAdapter {

    private final LayoutInflater layoutInflater;

    public DemoViewPagerAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    protected View getView(ViewGroup container, int position) {
        TextView view = (TextView) layoutInflater.inflate(com.novoda.landing_strip.R.layout.view_demo, container, false);
        view.setText(Data.values()[position].getTitle());
        return view;
    }

    @Override
    public int getCount() {
        return Data.values().length;
    }

}
