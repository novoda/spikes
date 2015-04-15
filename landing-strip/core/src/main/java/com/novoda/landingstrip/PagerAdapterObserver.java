package com.novoda.landingstrip;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;

class PagerAdapterObserver extends DataSetObserver {

    private final Notifiable notifiable;
    private PagerAdapter pagerAdapter;

    PagerAdapterObserver(Notifiable notifiable) {
        this.notifiable = notifiable;
    }

    void registerTo(PagerAdapter pagerAdapter) {
        if (this.pagerAdapter != null) {
            unregister();
        }
        this.pagerAdapter = pagerAdapter;
        pagerAdapter.registerDataSetObserver(this);
    }

    void unregister() {
        if (pagerAdapter == null) {
            return;
        }
        pagerAdapter.unregisterDataSetObserver(this);
    }

    @Override
    public void onChanged() {
        super.onChanged();
        notifiable.notify(pagerAdapter);
    }
}
