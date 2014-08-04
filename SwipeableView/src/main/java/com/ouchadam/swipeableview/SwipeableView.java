package com.ouchadam.swipeableview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class SwipeableView extends ViewPager {

    private static final int VIEW_COUNT = 2;
    private boolean canSwipe;

    public SwipeableView(Context context) {
        super(context);
        init(LayoutInflater.from(context));
    }

    public SwipeableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(LayoutInflater.from(context));
    }

    private void init(LayoutInflater layoutInflater) {
        canSwipe = true;
        setOnPageChangeListener(onPageChangeListener);
        setAdapter(new ViewAdapter(layoutInflater));
    }


    private final OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1) {
                canSwipe = false;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (canSwipe) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    private static class ViewAdapter extends PagerAdapter {

        private final LayoutInflater layoutInflater;

        private ViewAdapter(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getCount() {
            return VIEW_COUNT;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View inflate = getViewForPosition(container, position);
            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        private View getViewForPosition(ViewGroup container, int position) {
            return layoutInflater.inflate(position == 0 ? R.layout.swipe_page : R.layout.content, container, false);
        }

    }

}
