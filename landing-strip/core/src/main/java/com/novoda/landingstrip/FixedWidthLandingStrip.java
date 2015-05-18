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
    private final Attributes attributes;
    private final Paint indicatorPaint;
    private TabsPagerListener tabsPagerListener;

    public FixedWidthLandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        attributes = Attributes.readAttributes(context, attrs);
        TabsContainer tabsContainer = FixedWidthTabsContainer.newInstance(context, attributes, attrs);
        tabsPagerListener = TabsPagerListener.newInstance(tabsContainer, attributes, this, this);

        this.indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.getIndicatorColor()));
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        tabsPagerListener.setOnPageChangeListener(delegateOnPageChangeListener);
    }

    @Override
    public void attach(ViewPager viewPager) {
        tabsPagerListener.attach(viewPager);
    }

    @Override
    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        tabsPagerListener.attach(viewPager, pagerAdapter);
    }

    @Override
    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        tabsPagerListener.attach(viewPager, pagerAdapter, tabSetterUpper);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (tabsPagerListener.isEmpty()) {
            return;
        }

        drawIndicator(canvas, tabsPagerListener.calculateIndicatorCoordinates());
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
        tabsPagerListener.reregister();
    }

    @Override
    protected void onDetachedFromWindow() {
        tabsPagerListener.unregister();
        super.onDetachedFromWindow();
    }
}
