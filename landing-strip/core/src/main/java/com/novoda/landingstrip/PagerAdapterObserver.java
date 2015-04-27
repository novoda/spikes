package com.novoda.landingstrip;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;

class PagerAdapterObserver extends DataSetObserver {

    private final OnPagerAdapterChangedListener onPagerAdapterChangedListener;

    private PagerAdapter pagerAdapter;
    private boolean registered;

    PagerAdapterObserver(OnPagerAdapterChangedListener onPagerAdapterChangedListener) {
        this.onPagerAdapterChangedListener = onPagerAdapterChangedListener;
        this.registered = false;
    }

    void registerTo(PagerAdapter pagerAdapter) {
        if (this.pagerAdapter != null ||  registered) {
            unregister();
        }

        this.pagerAdapter = pagerAdapter;
        pagerAdapter.registerDataSetObserver(this);
        registered = true;
    }

    void reregister() {
        if (pagerAdapter == null ) {
            return;
        }
        registerTo(pagerAdapter);
    }

    void unregister() {
        if (pagerAdapter == null) {
            return;
        }
        pagerAdapter.unregisterDataSetObserver(this);
        registered = false;
    }

    @Override
    public void onChanged() {
        super.onChanged();
        onPagerAdapterChangedListener.onPagerAdapterChanged(pagerAdapter);
    }
}
