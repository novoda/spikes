package com.novoda.landingstrip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FixedWidthTabsContainer extends TabsContainer {

    static TabsContainer newInstance(Context context, Attributes attributes, AttributeSet attrs) {
        LinearLayout tabsContainerView = new LinearLayout(context);
        tabsContainerView.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams layoutParams = tabsContainerView.generateLayoutParams(attrs);
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        tabsContainerView.setLayoutParams(layoutParams);
        tabsContainerView.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);

        return new FixedWidthTabsContainer(tabsContainerView);
    }

    FixedWidthTabsContainer(LinearLayout tabsContainerView) {
        super(tabsContainerView);
    }

    @Override
    void addTab(View tabView, int position, CharSequence title) {
        super.addTab(tabView, position, title);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
        layoutParams.weight = 1;
        tabView.setLayoutParams(layoutParams);
    }
}
