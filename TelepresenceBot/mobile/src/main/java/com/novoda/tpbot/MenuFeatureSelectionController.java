package com.novoda.tpbot;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public final class MenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    @MenuRes
    private static final int FEATURE_MENU_RESOURCE = R.menu.feature_menu;

    private final MenuInflater menuInflater;
    private final SparseArray<FeatureSelectionPersistence> features;

    public static FeatureSelectionController<Menu, MenuItem> createFrom(Context context) {
        MenuInflater menuInflater = new MenuInflater(context);

        SparseArray<FeatureSelectionPersistence> features = new SparseArray<>();
        features.put(R.id.video_call_menu_item, VideoCallSharedPreferencesPersistence.newInstance(context));
        features.put(R.id.server_connection_menu_item, VideoCallSharedPreferencesPersistence.newInstance(context));

        return new MenuFeatureSelectionController(menuInflater, features);
    }

    private MenuFeatureSelectionController(MenuInflater menuInflater, SparseArray<FeatureSelectionPersistence> features) {
        this.menuInflater = menuInflater;
        this.features = features;
    }

    @Override
    public void attachFeatureSelectionTo(Menu toAttachTo) {
        menuInflater.inflate(FEATURE_MENU_RESOURCE, toAttachTo);
    }

    @Override
    public void handleFeatureToggle(MenuItem featureRepresentation) {
        FeatureSelectionPersistence featureSelectionPersistence = features.get(featureRepresentation.getItemId());

        if (featureSelectionPersistence == null) {
            throw new IllegalStateException("You must check if data is present before using handleFeatureToggle().");
        }

        if (featureSelectionPersistence.isFeatureEnabled()) {
            featureRepresentation.setChecked(false);
            featureSelectionPersistence.setFeatureDisabled();
            featureRepresentation.setIcon(android.R.drawable.checkbox_off_background);
        } else {
            featureRepresentation.setChecked(true);
            featureSelectionPersistence.setFeatureEnabled();
            featureRepresentation.setIcon(android.R.drawable.checkbox_on_background);
        }
    }

    @Override
    public boolean contains(MenuItem featureRepresentation) {
        return features.get(featureRepresentation.getItemId()) != null;
    }

}
