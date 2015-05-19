package com.novoda.landingstrip;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.novoda.landing_strip.R;
import com.novoda.landingstrip.setup.fragment.SmallDemoFragmentPagerAdapter;

public class FixedWidthTabActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_width_tab);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SmallDemoFragmentPagerAdapter(getSupportFragmentManager()));

        LandingStrip landingStrip = (LandingStrip) findViewById(R.id.landing_strip);
        landingStrip.attach(viewPager, viewPager.getAdapter());
    }

}
