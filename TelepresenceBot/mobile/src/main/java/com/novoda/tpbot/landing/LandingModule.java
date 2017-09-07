package com.novoda.tpbot.landing;

import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.FeatureSelectionPersistence;
import com.novoda.tpbot.R;
import com.novoda.tpbot.bot.service.ServiceConnectionSharedPreferencesPersistence;
import com.novoda.tpbot.bot.video.calling.VideoCallSharedPreferencesPersistence;

import dagger.Module;
import dagger.Provides;

@Module
public class LandingModule {

    @Provides
    FeatureSelectionController<Menu, MenuItem> provideController(
            VideoCallSharedPreferencesPersistence videoCallSharedPreferencesPersistence,
            ServiceConnectionSharedPreferencesPersistence serviceConnectionSharedPreferencesPersistence,
            MenuInflater menuInflater) {
        SparseArray<FeatureSelectionPersistence> features = new SparseArray<>();
        features.put(R.id.video_call_menu_item, videoCallSharedPreferencesPersistence);
        features.put(R.id.server_connection_menu_item, serviceConnectionSharedPreferencesPersistence);

        return new LandingMenuFeatureSelectionController(menuInflater, features);
    }

}
