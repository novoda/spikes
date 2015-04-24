package com.novoda.landingstrip;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;

class PagerAdapterObserver extends DataSetObserver {

    private final Notifiable notifiable;

    private PagerAdapter pagerAdapter;
    private boolean registered;

    PagerAdapterObserver(Notifiable notifiable) {
        this.notifiable = notifiable;
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
        notifiable.notify(pagerAdapter);
    }
}
