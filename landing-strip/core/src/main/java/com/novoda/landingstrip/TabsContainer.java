package com.novoda.landingstrip;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

class TabsContainer {

    private final LinearLayout tabsContainerView;

    static TabsContainer newInstance(Context context, Attributes attributes) {
        LinearLayout tabsContainerView = new LinearLayout(context);
        tabsContainerView.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainerView.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);

        return new TabsContainer(tabsContainerView);
    }

    TabsContainer(LinearLayout tabsContainerView) {
        this.tabsContainerView = tabsContainerView;
    }

    void attachTo(ViewGroup parent) {
        parent.addView(tabsContainerView, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

    }

    void clearTabs() {
        tabsContainerView.removeAllViews();
    }

    void addTab(View tabView, int position) {
        tabsContainerView.addView(tabView, position, tabView.getLayoutParams());
    }

    boolean hasTabs() {
        return tabsContainerView.getChildCount() > 0;
    }

    View getTabAt(int position) {
        return tabsContainerView.getChildAt(position);
    }

    boolean isEmpty() {
        return !hasTabs();
    }

    void startWatching(final ViewPager viewPager, final ViewPager.OnPageChangeListener onPageChangeListener) {
        if (hasTabs()) {
            getTabAt(0).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getTabAt(0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    viewPager.setOnPageChangeListener(onPageChangeListener);
                    viewPager.onRestoreInstanceState(viewPager.onSaveInstanceState());
                }
            });
        }
    }

    int getTabCount() {
        return tabsContainerView.getChildCount();
    }

    void setSelected(int position) {
        for (int index = 0; index < getTabCount(); index++) {
            View tab = getTabAt(position);
            tab.setSelected(index == position);
        }
    }

    int getParentWidth() {
        return ((View) tabsContainerView.getParent()).getWidth();
    }
}
