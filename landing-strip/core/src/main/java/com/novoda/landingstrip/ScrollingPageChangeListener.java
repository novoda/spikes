package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

class ScrollingPageChangeListener implements ViewPager.OnPageChangeListener {

    private final State state;
    private final ViewGroup tabsContainer;
    private final Scrollable scrollable;

    private boolean firstTimeAccessed = true;

    ScrollingPageChangeListener(State state, ViewGroup tabsContainer, Scrollable scrollable) {
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.scrollable = scrollable;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        handleAdapterSetBecausePageSelectedIsNotCalled(position);
        state.updatePosition(position);
        state.updatePositionOffset(positionOffset);

        if (tabsContainer.getChildCount() == 0) {
            return;
        }

        int scrollOffset = getHorizontalScrollOffset(position, positionOffset);
        float newScrollX = calculateScrollOffset(position, scrollOffset);

        scrollable.scrollTo((int) newScrollX);

        state.getDelegateOnPageListener().onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private void handleAdapterSetBecausePageSelectedIsNotCalled(int position) {
        if (firstTimeAccessed) {
            setSelected(position);
            firstTimeAccessed = false;
        }
    }

    private int getHorizontalScrollOffset(int position, float swipePositionOffset) {
        int tabWidth = tabsContainer.getChildAt(position).getWidth();
        return Math.round(swipePositionOffset * tabWidth);
    }

    private float calculateScrollOffset(int position, int scrollOffset) {
        View tabForPosition = tabsContainer.getChildAt(position);

        float tabStartX = tabForPosition.getLeft() + scrollOffset;
        int viewMiddleOffset = getTabParentWidth() / 2;
        float tabCenterOffset = ((tabForPosition.getRight() - tabForPosition.getLeft()) / 2f);

        return tabStartX - viewMiddleOffset + tabCenterOffset;
    }

    private int getTabParentWidth() {
        return ((View) tabsContainer.getParent()).getWidth();
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
