package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

class Foo implements ViewPager.OnPageChangeListener {

    private final State state;
    private final ViewGroup tabsContainer;
    private final IndicatorCoordinatesCalculator indicatorCoordinatesCalculator;
    private final HorizontalScrollView scrollView;

    private boolean firstTimeAccessed = true;

    Foo(State state, ViewGroup tabsContainer, IndicatorCoordinatesCalculator indicatorCoordinatesCalculator, HorizontalScrollView scrollView) {
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.indicatorCoordinatesCalculator = indicatorCoordinatesCalculator;
        this.scrollView = scrollView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (firstTimeAccessed) {
            setSelected(position);
            firstTimeAccessed = false;
        }

        int scrollOffset = getHorizontalScrollOffset(position, positionOffset);
        scrollToChild(position, positionOffset, scrollOffset);
        state.getDelegateOnPageListener().onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private int getHorizontalScrollOffset(int position, float swipePositionOffset) {
        int tabWidth = tabsContainer.getChildAt(position).getWidth();
        return Math.round(swipePositionOffset * tabWidth);
    }

    private void scrollToChild(int position, float positionOffset, int offset) {
        Coordinates indicatorCoordinates = indicatorCoordinatesCalculator.calculateIndicatorCoordinates(position, positionOffset, tabsContainer);
        float newScrollX = calculateScrollOffset(position, offset, indicatorCoordinates);

        state.updateIndicatorCoordinates(indicatorCoordinates);

        scrollView.scrollTo((int) newScrollX, 0);
    }

    private float calculateScrollOffset(int position, int offset, Coordinates indicatorCoordinates) {
        float newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
        newScrollX -= scrollView.getWidth() / 2;
        newScrollX += ((indicatorCoordinates.getEnd() - indicatorCoordinates.getStart()) / 2f);
        return newScrollX;
    }

    @Override
    public void onPageSelected(int position) {
        setSelected(position);
        state.getDelegateOnPageListener().onPageSelected(position);
    }

    private void setSelected(int position) {
        int childCount = tabsContainer.getChildCount();
        for (int index = 0; index < childCount; index++) {
            if (index == position) {
                tabsContainer.getChildAt(position).setSelected(true);
            } else {
                tabsContainer.getChildAt(index).setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int changedState) {
        state.getDelegateOnPageListener().onPageScrollStateChanged(changedState);
    }

}
