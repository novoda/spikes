package com.novoda.landingstrip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novoda.landingstrip.LandingStrip.TabSetterUpper;

public class TabsState implements OnPagerAdapterChangedListener {

    private static final int TAG_KEY_POSITION = R.id.ls__tag_key_position;

    private final Attributes attributes;
    private final LayoutInflater layoutInflater;
    private final State state;
    private final TabsContainer tabsContainer;
    private final ViewGroup parent;
    private final Scrollable scrollable;
    private final IndicatorCoordinatesCalculator indicatorCoordinatesCalculator;

    private ViewPager viewPager;
    private PagerAdapterObserver pagerAdapterObserver;

    public static TabsState newInstance(TabsContainer tabsContainer, Attributes attributes, ViewGroup parent, Scrollable scrollable) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        State state = new State();
        IndicatorCoordinatesCalculator indicatorCoordinatesCalculator = IndicatorCoordinatesCalculator.newInstance();

        state.updateDelegateOnPageListener(new ViewPager.SimpleOnPageChangeListener());
        state.updatePosition(0);
        state.updatePositionOffset(0);
        state.invalidateFastForwardPosition();

        tabsContainer.attachTo(parent);
        Builder builder = Builder.newInstance();
        builder.attributes(attributes)
                .indicatorCoordinatesCalculator(indicatorCoordinatesCalculator)
                .layoutInflater(layoutInflater)
                .parent(parent)
                .state(state)
                .scrollable(scrollable)
                .tabsContainer(tabsContainer);
        TabsState tabsState = builder.build();
        PagerAdapterObserver observer = new PagerAdapterObserver(tabsState);
        tabsState.setPagerAdapterObserver(observer);
        return tabsState;
    }

    public Coordinates calculateIndicatorCoordinates() {
        return indicatorCoordinatesCalculator.calculate(state.getPosition(), state.getPagePositionOffset(), tabsContainer);
    }

    private static class Builder {

        private Attributes attributes;
        private LayoutInflater layoutInflater;
        private State state;
        private TabsContainer tabsContainer;
        private ViewGroup parent;
        private Scrollable scrollable;
        private IndicatorCoordinatesCalculator indicatorCoordinatesCalculator;

        static Builder newInstance() {
            return new Builder();
        }

        public Builder attributes(Attributes attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder layoutInflater(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            return this;
        }

        public Builder state(State state) {
            this.state = state;
            return this;
        }

        public Builder tabsContainer(TabsContainer tabsContainer) {
            this.tabsContainer = tabsContainer;
            return this;
        }

        public Builder parent(ViewGroup parent) {
            this.parent = parent;
            return this;
        }

        public Builder scrollable(Scrollable scrollable) {
            this.scrollable = scrollable;
            return this;
        }

        public Builder indicatorCoordinatesCalculator(IndicatorCoordinatesCalculator indicatorCoordinatesCalculator) {
            this.indicatorCoordinatesCalculator = indicatorCoordinatesCalculator;
            return this;
        }

        public TabsState build() {
            return new TabsState(this);
        }
    }

    TabsState(Builder builder) {
        this.attributes = builder.attributes;
        this.layoutInflater = builder.layoutInflater;
        this.state = builder.state;
        this.tabsContainer = builder.tabsContainer;
        this.parent = builder.parent;
        this.scrollable = builder.scrollable;
        this.indicatorCoordinatesCalculator = builder.indicatorCoordinatesCalculator;
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
        this.viewPager = viewPager;
        tabsContainer.setTabSetterUpper(tabSetterUpper);

        pagerAdapterObserver.registerTo(pagerAdapter);
        pagerAdapterObserver.onChanged();
    }

    public void setPagerAdapterObserver(PagerAdapterObserver pagerAdapterObserver) {
        this.pagerAdapterObserver = pagerAdapterObserver;
    }

    @Override
    public void onPagerAdapterChanged(PagerAdapter pagerAdapter) {
        notifyDataSetChanged(pagerAdapter);
    }

    private void notifyDataSetChanged(PagerAdapter pagerAdapter) {
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

    public boolean isEmpty() {
        return tabsContainer.isEmpty();
    }

    public void reregister() {
        pagerAdapterObserver.reregister();
    }

    public void unregister() {
        pagerAdapterObserver.unregister();
    }
}
