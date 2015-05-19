package com.novoda.landingstrip;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novoda.landingstrip.LandingStrip.TabSetterUpper;

public class TabsOnPagerAdapterChangedListener implements OnPagerAdapterChangedListener {

    private static final int TAG_KEY_POSITION = R.id.ls__tag_key_position;

    private final Attributes attributes;
    private final State state;
    private final ViewGroup parent;
    private final Scrollable scrollable;
    private final TabsContainer tabsContainer;

    private PagerAdapterObserver pagerAdapterObserver;
    private ViewPager viewPager;

    TabsOnPagerAdapterChangedListener(State state, TabsContainer tabsContainer, Attributes attributes, ViewGroup parent, Scrollable scrollable) {
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.attributes = attributes;
        this.parent = parent;
        this.scrollable = scrollable;
    }

    public static TabsOnPagerAdapterChangedListener newInstance(State state, TabsContainer tabsContainer, Attributes attributes, ViewGroup parent, Scrollable scrollable) {

        tabsContainer.attachTo(parent);

        TabsOnPagerAdapterChangedListener tabsOnPagerAdapterChangedListener = new TabsOnPagerAdapterChangedListener(state, tabsContainer, attributes, parent, scrollable);
        PagerAdapterObserver observer = new PagerAdapterObserver(tabsOnPagerAdapterChangedListener);
        tabsOnPagerAdapterChangedListener.setPagerAdapterObserver(observer);
        return tabsOnPagerAdapterChangedListener;
    }

    @Override
    public void onPagerAdapterChanged(PagerAdapter pagerAdapter) {
        notifyDataSetChanged(pagerAdapter);
    }

    public void attach(ViewPager viewPager) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        attach(viewPager, pagerAdapter);
    }

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        attach(viewPager, pagerAdapter, null);
    }

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        this.viewPager = viewPager;
        tabsContainer.setTabSetterUpper(tabSetterUpper);

        pagerAdapterObserver.registerTo(pagerAdapter);
        pagerAdapterObserver.onChanged();
    }

    public void setPagerAdapterObserver(PagerAdapterObserver pagerAdapterObserver) {
        this.pagerAdapterObserver = pagerAdapterObserver;
    }

    public void reregister() {
        pagerAdapterObserver.reregister();
    }

    public void unregister() {
        pagerAdapterObserver.unregister();
    }

    private void notifyDataSetChanged(PagerAdapter pagerAdapter) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        tabsContainer.clearTabs();
        int tabCount = pagerAdapter.getCount();
        for (int position = 0; position < tabCount; position++) {
            CharSequence title = pagerAdapter.getPageTitle(position);
            View inflatedTabView = tabsContainer.inflateLayout(layoutInflater, attributes.getTabLayoutId());
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
