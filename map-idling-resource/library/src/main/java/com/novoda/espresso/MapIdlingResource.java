package com.novoda.espresso;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

/**
 * An IdlingResource that finds the {@link SupportMapFragment} in given {@link FragmentActivity}
 * and hook itself to map's async ready callback.
 */
public class MapIdlingResource implements IdlingResource, OnMapReadyCallback {

    private ResourceCallback callback;
    private boolean isMapReady = false;

    public static IdlingResource from(final FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        SupportMapFragment mapFragment = findSupportMapFragment(fragmentManager);

        Preconditions.checkNotNull(mapFragment, "Couldn't find SupportMapFragment in " + activity);

        return MainThread.createOnMainThread(mapFragment);
    }

    @Nullable
    private static SupportMapFragment findSupportMapFragment(FragmentManager supportFragmentManager) {
        List<Fragment> fragments = supportFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SupportMapFragment) {
                return (SupportMapFragment) fragment;
            }
            SupportMapFragment childMap = findSupportMapFragment(fragment.getChildFragmentManager());
            if (childMap != null) {
                return childMap;
            }
        }
        return null;
    }

    MapIdlingResource(SupportMapFragment mapFragment) {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (isMapReady) {
            return;
        }
        isMapReady = true;
        if (callback != null) {
            callback.onTransitionToIdle();
        }
    }

    @Override
    public String getName() {
        return MapIdlingResource.class.getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        return isMapReady;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }
}
