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
    private final IndicatorCoordinatesCalculator indicatorCoordinatesCalculator;

    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener delegateOnPageChangeListener;

    public LandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);

        this.attributes = Attributes.readAttributes(context, attrs);
        this.delegateOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener();
        this.layoutInflater = LayoutInflater.from(context);
        this.indicatorPaint = new Paint();
        this.state = new State();
        this.indicatorCoordinatesCalculator = IndicatorCoordinatesCalculator.newInstance();
        this.tabsContainer = new LinearLayout(context);

        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setPadding(attributes.getTabsPaddingLeft(), 0, attributes.getTabsPaddingRight(), 0);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.getIndicatorColour()));

        addView(tabsContainer, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener delegateOnPageChangeListener) {
        this.delegateOnPageChangeListener = delegateOnPageChangeListener;
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
        viewPager.setOnPageChangeListener(pageListener);
        notifyDataSetChanged(pagerAdapter, tabSetterUpper);
    }

    private final ViewPager.SimpleOnPageChangeListener pageListener = new ViewPager.SimpleOnPageChangeListener() {

        @SuppressWarnings("checkstyle:visibilityModifierCheck") // the scope is super small, setter and getter would be overkill
        private boolean firstTimeAccessed = true;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (firstTimeAccessed) {
                setSelected(position);
                firstTimeAccessed = false;
            }
            state.updatePosition(position);
            state.updateOffset(positionOffset);

            int offset = getHorizontalScrollOffset(position, positionOffset);
            scrollToChild(position, offset);
            delegateOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        private int getHorizontalScrollOffset(int position, float swipePositionOffset) {
            int tabWidth = tabsContainer.getChildAt(position).getWidth();
            return Math.round(swipePositionOffset * tabWidth);
        }

        private void scrollToChild(int position, int offset) {
            Coordinates indicatorCoordinates = indicatorCoordinatesCalculator.calculateIndicatorCoordinates(state, tabsContainer);
            float newScrollX = calculateScrollOffset(position, offset, indicatorCoordinates);

            scrollTo((int) newScrollX, 0);
        }

        private float calculateScrollOffset(int position, int offset, Coordinates indicatorCoordinates) {
            float newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
            newScrollX -= getWidth() / 2;
            newScrollX += ((indicatorCoordinates.getEnd() - indicatorCoordinates.getStart()) / 2f);
            return newScrollX;
        }

        @Override
        public void onPageSelected(int position) {
            setSelected(position);
            delegateOnPageChangeListener.onPageSelected(position);
        }

        private void setSelected(int position) {
            int childCount = tabsContainer.getChildCount();
            for (int index = 0; index < childCount; index++) {
                if (index == position) {
                    tabsContainer.getChildAt(position).setSelected(true);
                } else {
                    tabsContainer.getChildAt(index).setSelected(false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            delegateOnPageChangeListener.onPageScrollStateChanged(state);
        }

    };

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
        drawIndicator(canvas, indicatorCoordinatesCalculator.calculateIndicatorCoordinates(state, tabsContainer));
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
