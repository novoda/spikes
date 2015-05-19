package com.novoda.landingstrip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.landingstrip.LandingStrip.TabSetterUpper;

public class TabsContainer {
    private final LinearLayout tabsContainerView;
    private TabSetterUpper tabSetterUpper;

    public static TabsContainer newFixedWidthTabsContainer(Context context, Attributes attributes, AttributeSet attributeSet) {
        LinearLayout tabsContainerView = new LinearLayout(context);
        tabsContainerView.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams layoutParams = tabsContainerView.generateLayoutParams(attributeSet);
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        tabsContainerView.setLayoutParams(layoutParams);
        tabsContainerView.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);

        return new TabsContainer(tabsContainerView);

    }

    public static TabsContainer newScrollingTabsContainer(Context context, Attributes attributes, AttributeSet attributeSet) {
        LinearLayout tabsContainerView = new LinearLayout(context);
        tabsContainerView.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams layoutParams = tabsContainerView.generateLayoutParams(attributeSet);
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        tabsContainerView.setLayoutParams(layoutParams);
        tabsContainerView.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);

        return new TabsContainer(tabsContainerView);

    }

    TabsContainer(LinearLayout tabsContainerView) {
        this(tabsContainerView, SIMPLE_TEXT_TAB_SETTER_UPPER);
    }

    TabsContainer(LinearLayout tabsContainerView, @NonNull TabSetterUpper tabSetterUpper) {
        this.tabsContainerView = tabsContainerView;
        this.tabSetterUpper = tabSetterUpper;
    }

    public void setTabSetterUpper(TabSetterUpper tabSetterUpper) {
        if (tabSetterUpper == null) {
            this.tabSetterUpper = SIMPLE_TEXT_TAB_SETTER_UPPER;
        } else {
            this.tabSetterUpper = tabSetterUpper;
        }
    }

    void attachTo(ViewGroup parent) {
        parent.addView(tabsContainerView, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    View inflateLayout(LayoutInflater layoutInflater, int id) {
        return layoutInflater.inflate(id, tabsContainerView, false);
    }

    void clearTabs() {
        tabsContainerView.removeAllViews();
    }

    void addTab(View tabView, int position, CharSequence title) {
        setUpTab(tabView, position, title);
        tabsContainerView.addView(tabView, position, tabView.getLayoutParams());
    }

    void setUpTab(View tabView, int position, CharSequence title) {
        tabSetterUpper.setUp(position, title, tabView);
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
            getTabAt(0).getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            getTabAt(0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            viewPager.setOnPageChangeListener(onPageChangeListener);

                            onPageChangeListener.onPageScrolled(viewPager.getCurrentItem(), 0, 0);
                        }
                    });
        }
    }

    int getTabCount() {
        return tabsContainerView.getChildCount();
    }

    void setSelected(int position) {
        for (int index = 0; index < getTabCount(); index++) {
            View tab = getTabAt(index);
            tab.setSelected(index == position);
        }
    }

    int getParentWidth() {
        return ((View) tabsContainerView.getParent()).getWidth();
    }

    protected static final TabSetterUpper SIMPLE_TEXT_TAB_SETTER_UPPER = new TabSetterUpper() {
        @Override
        public View setUp(int position, CharSequence title, View inflatedTab) {
            ((TextView) inflatedTab).setText(title);
            inflatedTab.setFocusable(true);
            return inflatedTab;
        }
    };
}
