package com.novoda.landingstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LandingStrip extends HorizontalScrollView {

    private static final int TAG_KEY_POSITION = R.id.tag_key_position;

    private final Attributes attributes;
    private final LayoutInflater layoutInflater;
    private final Paint indicatorPaint;
    private final State state;
    private final LinearLayout tabsContainer;

    private ViewPager viewPager;

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

        state.updateDelegateOnPageListener(new ViewPager.SimpleOnPageChangeListener());
        state.updateIndicatorCoordinates(new MutableCoordinates());

        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.getIndicatorColour()));

        addView(tabsContainer, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        state.updateDelegateOnPageListener(delegateOnPageChangeListener);
    }

    public void setViewPager(ViewPager viewPager, PagerAdapter pagerAdapter) {
        setViewPager(viewPager, pagerAdapter, SIMPLE_TEXT_TAB);
    }

    private static final TabSetterUpper SIMPLE_TEXT_TAB = new TabSetterUpper() {
        @Override
        public View setUp(int position, CharSequence title, View inflatedTab) {
            ((TextView) inflatedTab).setText(title);
            inflatedTab.setFocusable(true);
            return inflatedTab;
        }
    };

    public void setViewPager(ViewPager viewPager, PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(new Foo(state, tabsContainer, IndicatorCoordinatesCalculator.newInstance(), this));
        notifyDataSetChanged(pagerAdapter, tabSetterUpper);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        invalidate();
    }

    private void notifyDataSetChanged(PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        tabsContainer.removeAllViews();
        int tabCount = pagerAdapter.getCount();
        for (int position = 0; position < tabCount; position++) {
            CharSequence title = pagerAdapter.getPageTitle(position);
            View inflatedTabView = layoutInflater.inflate(attributes.getTabLayoutId(), this, false);
            addTab(position, title, inflatedTabView, tabSetterUpper);
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
            viewPager.setCurrentItem(position);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas, state.getIndicatorCoordinates());
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

    public interface TabSetterUpper {
        View setUp(int position, CharSequence title, View inflatedTab);
    }
}
