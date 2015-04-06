package com.novoda.landingstrip;

import android.view.View;
import android.view.ViewGroup;

class IndicatorCoordinatesCalculator {

    private final MutableCoordinates drawCurrentTabCoordinates;
    private final MutableCoordinates drawNextTabCoordinates;
    private final MutableCoordinates drawMovingIndicatorCoordinates;

    static IndicatorCoordinatesCalculator newInstance() {
        MutableCoordinates drawCurrentTabCoordinates = new MutableCoordinates();
        MutableCoordinates drawNextTabCoordinates = new MutableCoordinates();
        MutableCoordinates drawMovingIndicatorCoordinates = new MutableCoordinates();

        return new IndicatorCoordinatesCalculator(drawCurrentTabCoordinates, drawNextTabCoordinates, drawMovingIndicatorCoordinates);
    }

    IndicatorCoordinatesCalculator(MutableCoordinates drawCurrentTabCoordinates, MutableCoordinates drawNextTabCoordinates, MutableCoordinates drawMovingIndicatorCoordinates) {
        this.drawCurrentTabCoordinates = drawCurrentTabCoordinates;
        this.drawNextTabCoordinates = drawNextTabCoordinates;
        this.drawMovingIndicatorCoordinates = drawMovingIndicatorCoordinates;
    }

    Coordinates calculate(int currentPosition, float positionOffset, ViewGroup tabsContainer) {
        View currentTab = tabsContainer.getChildAt(currentPosition);

        float currentTabStart = currentTab.getLeft();
        float currentTabEnd = currentTab.getRight();

        drawCurrentTabCoordinates.setStart(currentTabStart);
        drawCurrentTabCoordinates.setEnd(currentTabEnd);

        if (isScrolling(positionOffset, currentPosition, tabsContainer.getChildCount())) {
            View nextTab = tabsContainer.getChildAt(currentPosition + 1);

            drawNextTabCoordinates.setStart(nextTab.getLeft());
            drawNextTabCoordinates.setEnd(nextTab.getRight());

            return calculateMovingIndicatorCoordinates(positionOffset, drawCurrentTabCoordinates, drawNextTabCoordinates);
        }
        return drawCurrentTabCoordinates;
    }

    private boolean isScrolling(float positionOffset, int currentPosition, int childCount) {
        return positionOffset > 0f && currentPosition < childCount - 1;
    }

    private Coordinates calculateMovingIndicatorCoordinates(float positionOffset, Coordinates currentTab, Coordinates nextTab) {
        float nextTabPositionOffset = getFractionFrom(positionOffset);
        float indicatorStart = getIndicatorPosition(nextTab.getStart(), positionOffset, currentTab.getStart(), nextTabPositionOffset);
        float indicatorEnd = getIndicatorPosition(nextTab.getEnd(), positionOffset, currentTab.getEnd(), nextTabPositionOffset);

        drawMovingIndicatorCoordinates.setStart(indicatorStart);
        drawMovingIndicatorCoordinates.setEnd(indicatorEnd);

        return drawMovingIndicatorCoordinates;
    }

    private float getFractionFrom(float input) {
        return 1f - input;
    }

    private float getIndicatorPosition(float tabOffset, float positionOffset, float tabBoundary, float nextTabPositionOffset) {
        return positionOffset * tabOffset + nextTabPositionOffset * tabBoundary;
    }

}
