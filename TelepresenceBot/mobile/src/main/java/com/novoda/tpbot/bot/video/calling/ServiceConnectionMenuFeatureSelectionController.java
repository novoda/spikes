package com.novoda.tpbot.bot.video.calling;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.Menu;
import android.view.MenuItem;

import com.novoda.tpbot.R;
import com.novoda.tpbot.bot.service.ServiceConnectionSharedPreferencesPersistence;
import com.novoda.tpbot.feature_selection.FeatureSelectionController;
import com.novoda.tpbot.feature_selection.FeatureSelectionPersistence;

import static android.R.attr.key;

public final class ServiceConnectionMenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    private final int serviceConnectionResourceId;
    private final FeatureSelectionPersistence featureSelectionPersistence;

    public static FeatureSelectionController<Menu, MenuItem> createFrom(Context context) {
        FeatureSelectionPersistence featureSelectionPersistence = ServiceConnectionSharedPreferencesPersistence.newInstance(context);
        return new ServiceConnectionMenuFeatureSelectionController(R.id.server_connection_menu_item, featureSelectionPersistence);
    }

    private ServiceConnectionMenuFeatureSelectionController(@IdRes int serviceConnectionResourceId, FeatureSelectionPersistence featureSelectionPersistence) {
        this.serviceConnectionResourceId = serviceConnectionResourceId;
        this.featureSelectionPersistence = featureSelectionPersistence;
    }

    @Override
    public void attachFeatureSelectionTo(Menu toAttachTo) {
        MenuItem menuItem = toAttachTo.findItem(key);
        menuItem.setChecked(featureSelectionPersistence.isFeatureEnabled());
    }

    @Override
    public void handleFeatureToggle(MenuItem featureRepresentation) {
        if (featureRepresentation.getItemId() == serviceConnectionResourceId) {
            throw new IllegalStateException("You must check if data is present before using handleFeatureToggle().");
        }

        if (featureSelectionPersistence.isFeatureEnabled()) {
            featureRepresentation.setChecked(false);
            featureSelectionPersistence.setFeatureDisabled();
        } else {
            featureRepresentation.setChecked(true);
            featureSelectionPersistence.setFeatureEnabled();
        }
    }

    @Override
    public boolean contains(MenuItem featureRepresentation) {
        return featureRepresentation.getItemId() == serviceConnectionResourceId;
    }

}
