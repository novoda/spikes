package com.novoda.landingstrip;

import android.view.View;

class IndicatorCoordinatesCalculator {

    private final MutableCoordinates currentTabCoordinates;
    private final MutableCoordinates nextTabCoordinates;
    private final MutableCoordinates movingIndicatorCoordinates;

    static IndicatorCoordinatesCalculator newInstance() {
        MutableCoordinates drawCurrentTabCoordinates = new MutableCoordinates();
        MutableCoordinates drawNextTabCoordinates = new MutableCoordinates();
        MutableCoordinates drawMovingIndicatorCoordinates = new MutableCoordinates();

        return new IndicatorCoordinatesCalculator(drawCurrentTabCoordinates, drawNextTabCoordinates, drawMovingIndicatorCoordinates);
    }

    IndicatorCoordinatesCalculator(MutableCoordinates currentTabCoordinates, MutableCoordinates nextTabCoordinates, MutableCoordinates movingIndicatorCoordinates) {
        this.currentTabCoordinates = currentTabCoordinates;
        this.nextTabCoordinates = nextTabCoordinates;
        this.movingIndicatorCoordinates = movingIndicatorCoordinates;
    }

    Coordinates calculate(int currentPosition, float pagePositionOffset, TabsContainer tabsContainer) {
        View currentTab = tabsContainer.getTabAt(currentPosition);

        float currentTabStart = currentTab.getLeft();
        float currentTabEnd = currentTab.getRight();

        currentTabCoordinates.setStart(currentTabStart);
        currentTabCoordinates.setEnd(currentTabEnd);

        if (isScrolling(pagePositionOffset) && hasNextTab(currentPosition, tabsContainer.getTabCount())) {
            View nextTab = tabsContainer.getTabAt(currentPosition + 1);

            nextTabCoordinates.setStart(nextTab.getLeft());
            nextTabCoordinates.setEnd(nextTab.getRight());

            return calculateMovingIndicatorCoordinates(pagePositionOffset, currentTabCoordinates, nextTabCoordinates);
        }
        return currentTabCoordinates;
    }

    private boolean isScrolling(float positionOffset) {
        return positionOffset > 0f;
    }

    private boolean hasNextTab(int currentPosition, int childCount) {
        return currentPosition < childCount;

    }

    private Coordinates calculateMovingIndicatorCoordinates(float pagePositionOffset, Coordinates currentTab, Coordinates nextTab) {
        float nextTabPositionOffset = getFractionFrom(pagePositionOffset);
        float indicatorStart = getIndicatorPosition(nextTab.getStart(), pagePositionOffset, currentTab.getStart(), nextTabPositionOffset);
        float indicatorEnd = getIndicatorPosition(nextTab.getEnd(), pagePositionOffset, currentTab.getEnd(), nextTabPositionOffset);

        movingIndicatorCoordinates.setStart(indicatorStart);
        movingIndicatorCoordinates.setEnd(indicatorEnd);

        return movingIndicatorCoordinates;
    }

    private float getFractionFrom(float input) {
        return 1f - input;
    }

    private float getIndicatorPosition(float tabOffset, float pagePositionOffset, float tabBoundary, float nextTabPositionOffset) {
        return pagePositionOffset * tabOffset + nextTabPositionOffset * tabBoundary;
    }

}
