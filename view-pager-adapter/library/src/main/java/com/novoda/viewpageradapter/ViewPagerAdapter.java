package com.novoda.viewpageradapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class ViewPagerAdapter<V extends View> extends PagerAdapter {

    private final Map<V, Integer> instantiatedViews = new WeakHashMap<>();

    private ViewPagerAdapterState viewPagerAdapterState = ViewPagerAdapterState.newInstance();

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        V view = createView(container, position);
        bindView(view, position);

        view.setId(position);
        instantiatedViews.put(view, position);
        restoreViewState(position, view);
        container.addView(view);

        // key with which to associate this view
        return view;
    }

    /**
     * Inflate the view representing an item at the given position.
     * <p>
     * Do not add the view to the container, this is handled.
     *
     * @param container the parent view from which sizing information can be grabbed during inflation
     * @param position  the position of the data set that is to be represented by this view
     * @return the inflated and view
     */
    protected abstract V createView(ViewGroup container, int position);

    /**
     * Bind the view to the item at the given position.
     *
     * @param view     the view to bind
     * @param position the position of the data set that is to be represented by this view
     */
    protected abstract void bindView(V view, int position);

    private void restoreViewState(int position, View view) {
        SparseArray<Parcelable> parcelableSparseArray = viewPagerAdapterState.get(position);
        if (parcelableSparseArray == null) {
            return;
        }
        view.restoreHierarchyState(parcelableSparseArray);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            bindView(entry.getKey(), entry.getValue());
        }
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
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            View view = entry.getKey();
            saveViewState(position, view);
        }
        return viewPagerAdapterState;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state instanceof ViewPagerAdapterState) {
            this.viewPagerAdapterState = ((ViewPagerAdapterState) state);
        } else {
            super.restoreState(state, loader);
        }
    }

    @Override
    public final boolean isViewFromObject(View view, Object key) {
        return view.equals(key);
    }

}
