package com.novoda.landingstrip;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.novoda.landingstrip.LandingStrip.TabSetterUpper;

public class TabsOnPagerAdapterChangedListener implements OnPagerAdapterChangedListener {
    private final TabCreator tabCreator;
    private final TabsContainer tabsContainer;

    private PagerAdapterObserver pagerAdapterObserver;

    TabsOnPagerAdapterChangedListener(TabsContainer tabsContainer, TabCreator tabCreator) {
        this.tabsContainer = tabsContainer;
        this.tabCreator = tabCreator;
    }

    public static TabsOnPagerAdapterChangedListener newInstance(State state, TabsContainer tabsContainer, Attributes attributes, ViewGroup parent, Scrollable scrollable) {
        TabCreator tabCreator = new TabCreator(attributes, state, tabsContainer, parent, scrollable);

        tabsContainer.attachTo(parent);

        TabsOnPagerAdapterChangedListener tabsOnPagerAdapterChangedListener = new TabsOnPagerAdapterChangedListener(tabsContainer, tabCreator);
        PagerAdapterObserver observer = new PagerAdapterObserver(tabsOnPagerAdapterChangedListener);
        tabsOnPagerAdapterChangedListener.setPagerAdapterObserver(observer);
        return tabsOnPagerAdapterChangedListener;
    }

    @Override
    public void onPagerAdapterChanged(PagerAdapter pagerAdapter) {
        tabCreator.onPagerAdapterChanged(pagerAdapter);
    }

    public void attach(ViewPager viewPager) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        attach(viewPager, pagerAdapter);
    }

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        attach(viewPager, pagerAdapter, null);
    }

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        tabCreator.setViewPager(viewPager);
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
}
