package com.novoda.tpbot.landing;

import android.support.annotation.MenuRes;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.tpbot.Feature;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;

/***
 * TODO: Feature toggling can be redone. Think about from business point of view.
 *
 * Client has a FEATURE that they want to TOGGLE (enable/disable).
 *
 * FEATURE.toggle
 * FEATURE.isEnabled
 *
 * FEATURES a group of features represented as an immutable first class collection.
 * FEATURES.get(representation) returns a feature for a given representation.
 *
 * FEATURE_DISPLAYER displays the current state of the toggle in some android specific way.
 *
 * Presenter, knows about features. Features -> getFeature(representation)
 * Displayer knows how to display a particular feature.
 *
 */
final class LandingMenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    @MenuRes
    private static final int FEATURE_MENU_RESOURCE = R.menu.feature_menu;

    private final MenuInflater menuInflater;
    private final SparseArray<Feature> features;

    LandingMenuFeatureSelectionController(MenuInflater menuInflater, SparseArray<Feature> features) {
        this.menuInflater = menuInflater;
        this.features = features;
    }

    @Override
    public void attachFeaturesTo(Menu componentToAttachTo) {
        menuInflater.inflate(FEATURE_MENU_RESOURCE, componentToAttachTo);

        for (int index = 0; index < features.size(); index++) {
            int key = features.keyAt(index);

            MenuItem menuItem = componentToAttachTo.findItem(key);
            Feature feature = features.get(key);
            menuItem.setChecked(feature.isEnabled());
        }
    }

    @Override
    public void handleFeatureToggle(MenuItem featureRepresentation) {
        Feature feature = features.get(featureRepresentation.getItemId());

        if (feature == null) {
            throw new IllegalStateException("You must check if data is present before using handleFeatureToggle().");
        }

        if (feature.isEnabled()) {
            featureRepresentation.setChecked(false);
            feature.setDisabled();
        } else {
            featureRepresentation.setChecked(true);
            feature.setEnabled();
        }
    }

    @Override
    public boolean contains(MenuItem featureRepresentation) {
        return features.get(featureRepresentation.getItemId()) != null;
    }

}
