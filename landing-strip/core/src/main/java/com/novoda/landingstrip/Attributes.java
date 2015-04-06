package com.novoda.landingstrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;

class Attributes {

    @ColorRes
    private static final int DEFAULT_INDICATOR_COLOUR = R.color.ls__white;
    private static final int DEFAULT_INDICATOR_HEIGHT = 5;
    private static final int DEFAULT_TABS_PADDING = 0;

    private static final int MISSING_TAB_LAYOUT_ID = -1;

    @LayoutRes
    private final int tabLayoutId;

    @ColorRes
    private final int indicatorColor;

    private final int indicatorHeight;
    private final int tabsPaddingLeft;
    private final int tabsPaddingRight;

    static Attributes readAttributes(Context context, AttributeSet attrs) {
        TypedArray xml = context.obtainStyledAttributes(attrs, R.styleable.LandingStrip);

        try {
            int tabLayoutId = xml.getResourceId(R.styleable.LandingStrip_tabLayoutId, MISSING_TAB_LAYOUT_ID);

            throwIfTabLayoutIsMissing(tabLayoutId);

            int indicatorColour = xml.getResourceId(R.styleable.LandingStrip_indicatorColor, DEFAULT_INDICATOR_COLOUR);
            int indicatorHeight = xml.getDimensionPixelSize(R.styleable.LandingStrip_indicatorHeight, DEFAULT_INDICATOR_HEIGHT);
            int tabsPaddingLeft = xml.getDimensionPixelSize(R.styleable.LandingStrip_tabsLeftPadding, DEFAULT_TABS_PADDING);
            int tabsPaddingRight = xml.getDimensionPixelSize(R.styleable.LandingStrip_tabsRightPadding, DEFAULT_TABS_PADDING);

            return new Attributes(tabLayoutId, indicatorColour, indicatorHeight, tabsPaddingLeft, tabsPaddingRight);
        } finally {
            xml.recycle();
        }
    }

    private static void throwIfTabLayoutIsMissing(int tabLayoutId) {
        if (tabLayoutId != MISSING_TAB_LAYOUT_ID) {
            return;
        }
        throw new MissingTabLayoutIdExecption();
    }

    Attributes(@LayoutRes int tabLayoutId, @ColorRes int indicatorColor, int indicatorHeight, int tabsPaddingLeft, int tabsPaddingRight) {
        this.tabLayoutId = tabLayoutId;
        this.indicatorColor = indicatorColor;
        this.indicatorHeight = indicatorHeight;
        this.tabsPaddingLeft = tabsPaddingLeft;
        this.tabsPaddingRight = tabsPaddingRight;
    }

    int getTabLayoutId() {
        return tabLayoutId;
    }

    int getIndicatorColor() {
        return indicatorColor;
    }

    int getIndicatorHeight() {
        return indicatorHeight;
    }

    int getTabsPaddingLeft() {
        return tabsPaddingLeft;
    }

    int getTabsPaddingRight() {
        return tabsPaddingRight;
    }

    static class MissingTabLayoutIdExecption extends RuntimeException {
        MissingTabLayoutIdExecption() {
            super("No tabLayoutId has been set, you need to set one!");
        }
    }
}
