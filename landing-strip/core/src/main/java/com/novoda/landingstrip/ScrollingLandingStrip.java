package com.novoda.landingstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ScrollingLandingStrip extends HorizontalScrollView implements Scrollable, LandingStrip {

    private final Attributes attributes;
    private final Paint indicatorPaint;
    private TabsState tabsState;

    public ScrollingLandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);

        attributes = Attributes.readAttributes(context, attrs);
        TabsContainer tabsContainer = DynamicTabsContainer.newInstance(context, attributes, attrs);
        tabsState = TabsState.newInstance(tabsContainer, attributes, this, this);
        this.indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.getIndicatorColor()));
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        tabsState.setOnPageChangeListener(delegateOnPageChangeListener);
    }

    @Override
    public void attach(ViewPager viewPager) {
        tabsState.attach(viewPager);
    }

    @Override
    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        tabsState.attach(viewPager, pagerAdapter);
    }

    @Override
    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        tabsState.attach(viewPager, pagerAdapter, tabSetterUpper);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (tabsState.isEmpty()) {
            return;
        }

        drawIndicator(canvas, tabsState.calculateIndicatorCoordinates());
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
        scrollTo(x, getBottom());
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tabsState.reregister();
    }

    @Override
    protected void onDetachedFromWindow() {
        tabsState.unregister();
        super.onDetachedFromWindow();
    }
}
