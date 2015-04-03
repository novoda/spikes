package com.novoda.landingstrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LandingStrip extends HorizontalScrollView {

    private static final int INDICATOR_HEIGHT = 5;
    private static final int HORIZ_PADDING = 0;

    private static final int TAG_KEY_POSITION = R.id.tag_key_position;

    private final MutableCoordinates drawCurrentTabCoordinates;
    private final MutableCoordinates drawNextTabCoordinates;
    private final MutableCoordinates drawMovingIndicatorCoordinates;

    private final Attributes attributes;
    private final LayoutInflater layoutInflater;
    private final Paint indicatorPaint;
    private final State state;
    private final LinearLayout tabsContainer;

    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener delegateOnPageChangeListener;

    public LandingStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFillViewport(true);
        setWillNotDraw(false);

        this.attributes = readAttributes(context, attrs);
        this.delegateOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener();
        this.layoutInflater = LayoutInflater.from(context);
        this.indicatorPaint = new Paint();
        this.state = new State();
        this.drawCurrentTabCoordinates = new MutableCoordinates();
        this.drawNextTabCoordinates = new MutableCoordinates();
        this.drawMovingIndicatorCoordinates = new MutableCoordinates();
        this.tabsContainer = new LinearLayout(context);

        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setPadding(HORIZ_PADDING, 0, HORIZ_PADDING, 0);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setColor(getResources().getColor(attributes.indicatorColour));

        addView(tabsContainer, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private static Attributes readAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LandingStrip);
        int tabLayoutId = attributes.getResourceId(R.styleable.LandingStrip_tabLayoutId, -1);
        int indicatorColour = attributes.getResourceId(R.styleable.LandingStrip_indicatorColour, -1);
        attributes.recycle();
        return new Attributes(tabLayoutId, indicatorColour);
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
            int scrollOffset = Math.round(swipePositionOffset * tabWidth);
            return scrollOffset + HORIZ_PADDING;
        }

        @Override
        public void onPageSelected(int position) {
            setSelected(position);
            delegateOnPageChangeListener.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            delegateOnPageChangeListener.onPageScrollStateChanged(state);
        }
    };

    private void setSelected(int position) {
        int childCount = tabsContainer.getChildCount();
        for (int index = 0; index < childCount; index++) {
            tabsContainer.getChildAt(index).setSelected(false);
        }
        tabsContainer.getChildAt(position).setSelected(true);
    }

    private void notifyDataSetChanged(PagerAdapter pagerAdapter, TabSetterUpper tabSetterUpper) {
        tabsContainer.removeAllViews();
        int tabCount = pagerAdapter.getCount();
        for (int position = 0; position < tabCount; position++) {
            CharSequence title = pagerAdapter.getPageTitle(position);
            View inflatedTabView = layoutInflater.inflate(attributes.tabLayoutId, this, false);
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
        if (isInEditMode() || tabsContainer.getChildCount() == 0) {
            return;
        }

        Coordinates indicatorCoordinates = getIndicatorStartAndEndCoordinates();
        drawIndicator(canvas, indicatorCoordinates);
    }

    private Coordinates getIndicatorStartAndEndCoordinates() {
        int currentPosition = state.getPosition();
        View currentTab = tabsContainer.getChildAt(currentPosition);

        float currentTabStart = currentTab.getLeft();
        float currentTabEnd = currentTab.getRight();
        float positionOffset = state.getOffset();

        drawCurrentTabCoordinates.setStart(currentTabStart);
        drawCurrentTabCoordinates.setEnd(currentTabEnd);

        if (isScrolling(positionOffset, currentPosition)) {
            View nextTab = tabsContainer.getChildAt(currentPosition + 1);

            drawNextTabCoordinates.setStart(nextTab.getLeft());
            drawNextTabCoordinates.setEnd(nextTab.getRight());

            return calculateMovingIndicatorCoordinates(positionOffset, drawCurrentTabCoordinates, drawNextTabCoordinates);
        }
        return drawCurrentTabCoordinates;
    }

    private boolean isScrolling(float positionOffset, int currentPosition) {
        return positionOffset > 0f && currentPosition < tabsContainer.getChildCount() - 1;
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

    protected void drawIndicator(Canvas canvas, Coordinates indicatorCoordinates) {
        int height = getHeight();
        canvas.drawRect(
                indicatorCoordinates.getStart(),
                height - INDICATOR_HEIGHT,
                indicatorCoordinates.getEnd(),
                height,
                indicatorPaint
        );
    }

    private void scrollToChild(int position, int offset) {
        Coordinates lines = getIndicatorStartAndEndCoordinates();
        float newScrollX = calculateScrollOffset(position, offset, lines);

        scrollTo((int) newScrollX, 0);
    }

    private float calculateScrollOffset(int position, int offset, Coordinates lines) {
        float newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
        newScrollX -= getWidth() / 2;
        newScrollX += ((lines.getEnd() - lines.getStart()) / 2f);
        return newScrollX;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        invalidate();
    }

    static class State {

        private float offset;
        private int position;

        void updateOffset(float offset) {
            this.offset = offset;
        }

        void updatePosition(int position) {
            this.position = position;
        }

        float getOffset() {
            return offset;
        }

        int getPosition() {
            return position;
        }
    }

    static class Attributes {

        @LayoutRes
        final int tabLayoutId;

        @ColorRes
        final int indicatorColour;

        Attributes(@LayoutRes int tabLayoutId, @ColorRes int indicatorColour) {
            this.tabLayoutId = tabLayoutId;
            this.indicatorColour = indicatorColour;
        }
    }

    public interface TabSetterUpper {
        View setUp(int position, CharSequence title, View inflatedTab);
    }
}
