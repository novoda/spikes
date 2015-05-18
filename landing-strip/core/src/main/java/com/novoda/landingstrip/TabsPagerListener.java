package com.novoda.landingstrip;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.novoda.landingstrip.LandingStrip.TabSetterUpper;

public class TabsPagerListener implements OnPagerAdapterChangedListener {
    private final State state;
    private final TabCreator tabCreator;
    private final TabsContainer tabsContainer;

    private PagerAdapterObserver pagerAdapterObserver;

    public static TabsPagerListener newInstance(TabsContainer tabsContainer, Attributes attributes, ViewGroup parent, Scrollable scrollable) {
        State state = State.newInstance();

        state.updateDelegateOnPageListener(new ViewPager.SimpleOnPageChangeListener());
        state.updatePosition(0);
        state.updatePositionOffset(0);
        state.invalidateFastForwardPosition();

        TabCreator tabCreator = new TabCreator(attributes, state, tabsContainer, parent, scrollable);

        tabsContainer.attachTo(parent);

        TabsPagerListener tabsPagerListener = new TabsPagerListener(state, tabsContainer, tabCreator);
        PagerAdapterObserver observer = new PagerAdapterObserver(tabsPagerListener);
        tabsPagerListener.setPagerAdapterObserver(observer);
        return tabsPagerListener;
    }

    public Coordinates calculateIndicatorCoordinates() {
        return state.getIndicatorPosition(tabsContainer);
    }

    @Override
    public void onPagerAdapterChanged(PagerAdapter pagerAdapter) {
        tabCreator.onPagerAdapterChanged(pagerAdapter);
    }

    TabsPagerListener(State state, TabsContainer tabsContainer, TabCreator tabCreator) {
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.tabCreator = tabCreator;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        state.updateDelegateOnPageListener(delegateOnPageChangeListener);
    }

    public void attach(ViewPager viewPager) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        attach(viewPager, pagerAdapter);
    }

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        attach(viewPager, pagerAdapter, TabsContainer.getDefaultTabSetterUpper());
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

    public boolean isEmpty() {
        return tabsContainer.isEmpty();
    }
}
