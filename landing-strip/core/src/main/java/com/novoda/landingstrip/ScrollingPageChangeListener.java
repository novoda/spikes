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
            if (fastForwardPositionReached(position, positionOffsetPixels)) {
                state.invalidateFastForwardPosition();
            }
            fastForward();
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

    private boolean fastForwardPositionReached(int position, int positionOffsetPixels) {
        return state.getFastForwardPosition() == position && positionOffsetPixels == 0;
    }

    private void fastForward() {
        scroll(state.getFastForwardPosition(), 0);
    }

    private void scroll(int position, float positionOffset) {
        int scrollOffset = getHorizontalScrollOffset(position, positionOffset);
        float newScrollX = calculateScrollOffset(position, scrollOffset);

        state.updatePosition(position);
        state.updatePositionOffset(positionOffset);

        scrollable.scrollTo((int) newScrollX);
    }

    private int getHorizontalScrollOffset(int position, float swipePositionOffset) {
        int tabWidth = tabsContainer.getTabAt(position).getWidth();
        return Math.round(swipePositionOffset * tabWidth);
    }

    private float calculateScrollOffset(int position, int scrollOffset) {
        View tabForPosition = tabsContainer.getTabAt(position);

        float tabStartX = tabForPosition.getLeft() + scrollOffset;
        int viewMiddleOffset = getTabParentWidth() / 2;
        float tabCenterOffset = ((tabForPosition.getRight() - tabForPosition.getLeft()) / 2f);

        return tabStartX - viewMiddleOffset + tabCenterOffset;
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
