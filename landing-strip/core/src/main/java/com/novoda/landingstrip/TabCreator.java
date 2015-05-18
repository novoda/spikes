package com.novoda.landingstrip;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class TabCreator {

    private static final int TAG_KEY_POSITION = R.id.ls__tag_key_position;

    private final Attributes attributes;
    private final State state;
    private final TabsContainer tabsContainer;
    private final ViewGroup parent;
    private final Scrollable scrollable;

    private ViewPager viewPager = null;

    TabCreator(Attributes attributes, State state, TabsContainer tabsContainer, ViewGroup parent, Scrollable scrollable) {
        this.attributes = attributes;
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.parent = parent;
        this.scrollable = scrollable;
    }

    public void onPagerAdapterChanged(PagerAdapter pagerAdapter) {
        notifyDataSetChanged(pagerAdapter);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    private void notifyDataSetChanged(PagerAdapter pagerAdapter) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        tabsContainer.clearTabs();
        int tabCount = pagerAdapter.getCount();
        for (int position = 0; position < tabCount; position++) {
            CharSequence title = pagerAdapter.getPageTitle(position);
            View inflatedTabView = layoutInflater.inflate(attributes.getTabLayoutId(), parent, false);
            addTab(position, title, inflatedTabView);
        }
        tabsContainer.startWatching(viewPager, new ScrollingPageChangeListener(state, tabsContainer, scrollable));
    }

    private void addTab(final int position, CharSequence title, View tabView) {
        tabsContainer.addTab(tabView, position, title);
        tabView.setOnClickListener(onTabClick);
        tabView.setTag(TAG_KEY_POSITION, position);
    }

    private final View.OnClickListener onTabClick = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View view) {
            int position = (int) view.getTag(TAG_KEY_POSITION);
            state.updateFastForwardPosition(position);
            viewPager.setCurrentItem(position);
        }
    };

}
