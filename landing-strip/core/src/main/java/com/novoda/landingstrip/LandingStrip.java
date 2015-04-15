package com.novoda.landingstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LandingStrip extends HorizontalScrollView implements Scrollable, Notifiable {

    private static final int TAG_KEY_POSITION = R.id.ls__tag_key_position;

    private final Attributes attributes;
    private final LayoutInflater layoutInflater;
    private final Paint indicatorPaint;
    private final State state;
    private final LinearLayout tabsContainer;
    private final IndicatorCoordinatesCalculator indicatorCoordinatesCalculator;
    private final PagerAdapterObserver pagerAdapterObserver;

    private ViewPager viewPager;
    private TabSetterUpper tabSetterUpper;

    public LandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);

        this.attributes = Attributes.readAttributes(context, attrs);
        this.layoutInflater = LayoutInflater.from(context);
        this.indicatorPaint = new Paint();
        this.state = new State();
        this.tabsContainer = new LinearLayout(context);
        this.indicatorCoordinatesCalculator = IndicatorCoordinatesCalculator.newInstance();
        this.pagerAdapterObserver = new PagerAdapterObserver(this);

        state.updateDelegateOnPageListener(new ViewPager.SimpleOnPageChangeListener());
        state.updatePosition(0);
        state.updatePositionOffset(0);
        state.invalidateFastForwardPosition();

        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.getIndicatorColor()));

        addView(tabsContainer, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        state.updateDelegateOnPageListener(delegateOnPageChangeListener);
    }

    public void attach(ViewPager viewPager) {
        attach(viewPager, viewPager.getAdapter());
    }

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter) {
        attach(viewPager, pagerAdapter, SIMPLE_TEXT_TAB);
    }

    private static final TabSetterUpper SIMPLE_TEXT_TAB = new TabSetterUpper() {
        @Override
        public View setUp(int position, CharSequence title, View inflatedTab) {
            ((TextView) inflatedTab).setText(title);
            inflatedTab.setFocusable(true);
            return inflatedTab;
        }
    };

    public void attach(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        this.viewPager = viewPager;
        this.tabSetterUpper = tabSetterUpper;
        pagerAdapterObserver.registerTo(pagerAdapter);
        notifyDataSetChanged(pagerAdapter);
    }

    private void notifyDataSetChanged(PagerAdapter pagerAdapter) {
        tabsContainer.removeAllViews();
        int tabCount = pagerAdapter.getCount();
        for (int position = 0; position < tabCount; position++) {
            CharSequence title = pagerAdapter.getPageTitle(position);
            View inflatedTabView = layoutInflater.inflate(attributes.getTabLayoutId(), this, false);
            addTab(position, title, inflatedTabView, tabSetterUpper);
        }
        restoreTabStateFrom(viewPager);
    }

    private void restoreTabStateFrom(final ViewPager viewPager) {
        if (tabsContainer.getChildCount() > 0) {
            tabsContainer.getChildAt(0).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    tabsContainer.getChildAt(0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    viewPager.setOnPageChangeListener(new ScrollingPageChangeListener(state, tabsContainer, LandingStrip.this));
                    viewPager.onRestoreInstanceState(viewPager.onSaveInstanceState());
                }
            });
        }
    }

    private void addTab(final int position, CharSequence title, View tabView, TabSetterUpper tabSetterUpper) {
        tabView = tabSetterUpper.setUp(position, title, tabView);
        tabsContainer.addView(tabView, position, tabView.getLayoutParams());
        tabView.setOnClickListener(onTabClick);
        tabView.setTag(TAG_KEY_POSITION, position);
    }

    private final OnClickListener onTabClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(TAG_KEY_POSITION);
            state.updateFastForwardPosition(position);
            viewPager.setCurrentItem(position);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (tabsContainer.getChildCount() == 0) {
            return;
        }

        drawIndicator(canvas, indicatorCoordinatesCalculator.calculate(state.getPosition(), state.getPagePositionOffset(), tabsContainer));
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
        scrollTo(x, 0);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        pagerAdapterObserver.unregister();
        super.onDetachedFromWindow();
    }

    @Override
    public void notify(PagerAdapter pagerAdapter) {
        notifyDataSetChanged(pagerAdapter);
    }

    public interface TabSetterUpper {
        View setUp(int position, CharSequence title, View inflatedTab);
    }
}
