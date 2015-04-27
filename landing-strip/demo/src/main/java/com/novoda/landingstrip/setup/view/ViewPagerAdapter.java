package com.novoda.landingstrip.setup.view;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class ViewPagerAdapter extends PagerAdapter {

    private final Map<View, Integer> instantiatedViews = new WeakHashMap<>();

    private ViewPager viewPager;
    private int position = PagerAdapter.POSITION_NONE;
    private ViewPagerAdapterState viewPagerAdapterState = ViewPagerAdapterState.newInstance();

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged(); // called when we have updated the adapter, so it's safe to setCurrentItem
        int currentPosition = viewPagerAdapterState.getCurrentPosition();
        viewPager.setCurrentItem(currentPosition, false);
    }

    @Override
    public final View instantiateItem(ViewGroup container, int position) {
        this.viewPager = (ViewPager) container;

        View view = getView(container, position);
        view.setId(position);
        instantiatedViews.put(view, position);
        restoreViewState(position, view);
        container.addView(view);
        return view;
    }

    /**
     * Inflate and bind data to the view representing an item at the given position.
     * <p/>
     * Do not add the view to the container, this is handled.
     *
     * @param container the parent view from which sizing information can be grabbed during inflation
     * @param position  the position of the data set that is to be represented by this view
     * @return the inflated and data-bound view
     */
    protected abstract View getView(ViewGroup container, int position);

    private void restoreViewState(int position, View view) {
        SparseArray<Parcelable> parcelableSparseArray = viewPagerAdapterState.get(position);
        if (parcelableSparseArray == null) {
            return;
        }
        view.restoreHierarchyState(parcelableSparseArray);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        saveViewState(position, view);
        container.removeView(view);
    }

    private void saveViewState(int position, View view) {
        SparseArray<Parcelable> viewState = new SparseArray<>();
        view.saveHierarchyState(viewState);
        viewPagerAdapterState.put(position, viewState);
    }

    @Override
    public Parcelable saveState() {
        viewPagerAdapterState.setCurrentPosition(position);
        for (Map.Entry<View, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            View view = entry.getKey();
            saveViewState(position, view);
        }
        return viewPagerAdapterState;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (!(state instanceof ViewPagerAdapterState)) {
            super.restoreState(state, loader);
        } else {
            this.viewPagerAdapterState = ((ViewPagerAdapterState) state);
        }
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void setPrimaryItem(ViewGroup viewPager, int position, Object view) {
        if (this.position != position) {
            onPrimaryItemChanged(position);
        }
    }

    private void onPrimaryItemChanged(int position) {
        this.position = position;
    }

}