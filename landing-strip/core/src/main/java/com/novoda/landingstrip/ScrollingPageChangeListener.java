package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;
import android.view.View;

class ScrollingPageChangeListener implements ViewPager.OnPageChangeListener {

    private final State state;
    private final TabsContainer tabsContainer;
    private final Scrollable scrollable;

    private boolean firstTimeAccessed = true;

    ScrollingPageChangeListener(State state, TabsContainer tabsContainer, Scrollable scrollable) {
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.scrollable = scrollable;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        handleAdapterSetBecausePageSelectedIsNotCalled(position);

        if (state.fastForwardPositionIsValid()) {
            fastForward();
            if (fastForwardPositionReached(position, positionOffset)) {
                state.invalidateFastForwardPosition();
            }
        } else {
            scroll(position, positionOffset);
        }

        state.getDelegateOnPageListener().onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private void handleAdapterSetBecausePageSelectedIsNotCalled(int position) {
        if (firstTimeAccessed) {
            tabsContainer.setSelected(position);
            firstTimeAccessed = false;
        }
    }

    private boolean fastForwardPositionReached(int position, float positionOffset) {
        return position == state.getFastForwardPosition() && positionOffset == 0F;
    }

    private void fastForward() {
        scroll(state.getFastForwardPosition(), 0);
    }

    private void scroll(int position, float positionOffset) {
        int scrollOffset = getHorizontalScrollOffset(position, positionOffset);
        float newScrollX = calculateScrollOffset(position, scrollOffset, positionOffset);

        state.updatePosition(position);
        state.updatePositionOffset(positionOffset);

        scrollable.scrollTo((int) newScrollX);
    }

    private int getHorizontalScrollOffset(int position, float swipePositionOffset) {
        int tabWidth = tabsContainer.getTabAt(position).getWidth();
        return Math.round(swipePositionOffset * tabWidth);
    }

    private float calculateScrollOffset(int position, int scrollOffset, float pagerOffset) {
        View tabForPosition = tabsContainer.getTabAt(position);

        float tabStartX = tabForPosition.getLeft() + scrollOffset;

        int viewMiddleOffset = getTabParentWidth() / 2;
        float tabCenterOffset = (tabForPosition.getRight() - tabForPosition.getLeft()) * 0.5F;

        float nextTabDelta = getNextTabDelta(position, pagerOffset, tabForPosition);

        return tabStartX - viewMiddleOffset + tabCenterOffset + nextTabDelta;
    }

    private float getNextTabDelta(int position, float pagerOffset, View tabForPosition) {
        if (tabsContainer.getTabCount() - 1 >= position + 1) {
            return (((tabsContainer.getTabAt(position + 1).getWidth()) - tabForPosition.getWidth()) * pagerOffset) * 0.5F;
        }
        return 0F;
    }

    private int getTabParentWidth() {
        return tabsContainer.getParentWidth();
    }

    @Override
    public void onPageSelected(int position) {
        tabsContainer.setSelected(position);
        state.getDelegateOnPageListener().onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int changedState) {
        state.getDelegateOnPageListener().onPageScrollStateChanged(changedState);
    }

}
