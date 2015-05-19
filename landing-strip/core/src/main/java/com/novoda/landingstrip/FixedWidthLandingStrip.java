package com.novoda.landingstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class FixedWidthLandingStrip extends LinearLayout implements Scrollable, LandingStrip {

    private final State state;
    private final Attributes attributes;
    private final Paint indicatorPaint;
    private final TabsContainer tabsContainer;
    private final IndicatorCoordinatesCalculator indicatorCoordinatesCalculator;
    private TabsOnPagerAdapterChangedListener tabsOnPagerAdapterChangedListener;

    public FixedWidthLandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        state = new State();
        state.updateDelegateOnPageListener(new ViewPager.SimpleOnPageChangeListener());
        state.updatePosition(0);
        state.updatePositionOffset(0);
        state.invalidateFastForwardPosition();

        attributes = Attributes.readAttributes(context, attrs);
        tabsContainer = FixedWidthTabsContainer.newInstance(context, attributes, attrs);
        tabsOnPagerAdapterChangedListener = TabsOnPagerAdapterChangedListener.newInstance(state, tabsContainer, attributes, this, this);

        this.indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.getIndicatorColor()));

        indicatorCoordinatesCalculator = IndicatorCoordinatesCalculator.newInstance(tabsContainer);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        state.updateDelegateOnPageListener(delegateOnPageChangeListener);
    }

    @Override
    public void attach(ViewPager viewPager) {
        tabsOnPagerAdapterChangedListener.attach(viewPager);
    }

    @Override
    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        tabsOnPagerAdapterChangedListener.attach(viewPager, pagerAdapter);
    }

    @Override
    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        tabsOnPagerAdapterChangedListener.attach(viewPager, pagerAdapter, tabSetterUpper);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (tabsContainer.isEmpty()) {
            return;
        }

        drawIndicator(canvas, indicatorCoordinatesCalculator.calculate(state));
    }

    protected void drawIndicator(Canvas canvas, Coordinates indicatorCoordinates) {
        int height = getHeight();
        canvas.drawRect(
                indicatorCoordinates.getStart(),
                height - attributes.getIndicatorHeight(),
                indicatorCoordinates.getEnd(),
                height,
                indicatorPaint
        );
    }

    @Override
    public void scrollTo(int x) {
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tabsOnPagerAdapterChangedListener.reregister();
    }

    @Override
    protected void onDetachedFromWindow() {
        tabsOnPagerAdapterChangedListener.unregister();
        super.onDetachedFromWindow();
    }
}
