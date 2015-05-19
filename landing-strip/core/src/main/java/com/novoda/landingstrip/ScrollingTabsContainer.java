package com.novoda.landingstrip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ScrollingTabsContainer extends TabsContainer {

    static TabsContainer newInstance(Context context, Attributes attributes, AttributeSet attrs) {
        LinearLayout tabsContainerView = new LinearLayout(context);
        tabsContainerView.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams layoutParams = tabsContainerView.generateLayoutParams(attrs);
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        tabsContainerView.setLayoutParams(layoutParams);
        tabsContainerView.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);

        return new ScrollingTabsContainer(tabsContainerView);
    }

    ScrollingTabsContainer(LinearLayout tabsContainerView) {
        super(tabsContainerView);
    }
}
