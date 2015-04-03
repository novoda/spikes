package com.novoda.landingstrip;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novoda.landing_strip.R;

public class DemoActivity2 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_2);

        LandingStrip landingStrip = (LandingStrip) findViewById(R.id.landing_strip);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new DemoPagerAdapter(getSupportFragmentManager()));

        landingStrip.setViewPager(viewPager, viewPager.getAdapter(), busyTabs);
    }

    private final LandingStrip.TabSetterUpper busyTabs = new LandingStrip.TabSetterUpper() {
        @Override
        public View setUp(int position, CharSequence title, View inflatedTab) {
            ((TextView) inflatedTab.findViewById(R.id.tab_2_title)).setText("" + position);
            ((ImageView) inflatedTab.findViewById(R.id.tab_2_content)).setImageResource(Data.values()[position].resId);

            return inflatedTab;
        }
    };

}
