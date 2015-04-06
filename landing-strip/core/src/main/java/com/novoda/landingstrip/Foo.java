package com.novoda.landingstrip;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

class Foo implements ViewPager.OnPageChangeListener {

    private final State state;
    private final ViewGroup tabsContainer;
    private final HorizontalScrollView scrollView;

    private boolean firstTimeAccessed = true;

    Foo(State state, ViewGroup tabsContainer, HorizontalScrollView scrollView) {
        this.state = state;
        this.tabsContainer = tabsContainer;
        this.scrollView = scrollView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (firstTimeAccessed) {
            setSelected(position);
            firstTimeAccessed = false;
        }

        state.updatePosition(position);
        state.updatePositionOffset(positionOffset);

        int scrollOffset = getHorizontalScrollOffset(position, positionOffset);
        float newScrollX = calculateScrollOffset(position, scrollOffset);

        scrollView.scrollTo((int) newScrollX, 0);

        state.getDelegateOnPageListener().onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private int getHorizontalScrollOffset(int position, float swipePositionOffset) {
        int tabWidth = tabsContainer.getChildAt(position).getWidth();
        return Math.round(swipePositionOffset * tabWidth);
    }

    private float calculateScrollOffset(int position, int scrollOffset) {
        View tabForPosition = tabsContainer.getChildAt(position);

        float tabStartX = tabForPosition.getLeft() + scrollOffset;
        int  viewMiddleOffset = scrollView.getWidth() / 2;
        float tabCenterOffset = ((tabForPosition.getRight() - tabForPosition.getLeft()) / 2f);

        return tabStartX - viewMiddleOffset + tabCenterOffset;
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
