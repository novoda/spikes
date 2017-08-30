package com.novoda.tpbot.bot.video.calling;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.Menu;
import android.view.MenuItem;

import com.novoda.tpbot.R;
import com.novoda.tpbot.feature_selection.FeatureSelectionController;
import com.novoda.tpbot.feature_selection.FeatureSelectionPersistence;

import static android.R.attr.key;

public final class VideoCallMenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    private final int videoCallResourceId;
    private final FeatureSelectionPersistence featureSelectionPersistence;

    public static FeatureSelectionController<Menu, MenuItem> createFrom(Context context) {
        VideoCallSharedPreferencesPersistence videoCallSharedPreferencesPersistence = VideoCallSharedPreferencesPersistence.newInstance(context);
        return new VideoCallMenuFeatureSelectionController(R.id.video_call_menu_item, videoCallSharedPreferencesPersistence);
    }

    VideoCallMenuFeatureSelectionController(@IdRes int videoCallResourceId, FeatureSelectionPersistence featureSelectionPersistence) {
        this.videoCallResourceId = videoCallResourceId;
        this.featureSelectionPersistence = featureSelectionPersistence;
    }

    @Override
    public void attachFeatureSelectionTo(Menu toAttachTo) {
        MenuItem menuItem = toAttachTo.findItem(key);
        menuItem.setChecked(featureSelectionPersistence.isFeatureEnabled());
    }

    @Override
    public void handleFeatureToggle(MenuItem featureRepresentation) {
        if (featureRepresentation.getItemId() == videoCallResourceId) {
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
        return featureRepresentation.getItemId() == videoCallResourceId;
    }

}
