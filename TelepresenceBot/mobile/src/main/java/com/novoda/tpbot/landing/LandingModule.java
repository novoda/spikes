package com.novoda.tpbot.landing;

import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.tpbot.Feature;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.Features;
import com.novoda.tpbot.R;
import com.novoda.tpbot.bot.service.ServiceConnectionFeature;
import com.novoda.tpbot.bot.video.calling.VideoCallFeature;

import java.util.HashMap;

import dagger.Module;
import dagger.Provides;

@Module
public class LandingModule {

    @Provides
    FeatureSelectionController<Menu, MenuItem> provideController(
            VideoCallFeature videoCallFeaturePersistence,
            ServiceConnectionFeature serviceConnectionFeature,
            MenuInflater menuInflater) {
        SparseArray<Feature> features = new SparseArray<>();
        features.put(R.id.video_call_menu_item, videoCallFeaturePersistence);
        features.put(R.id.server_connection_menu_item, serviceConnectionFeature);

        return new LandingMenuFeatureSelectionController(menuInflater, features);
    }

    @Provides
    Features provideFeatures(VideoCallFeature videoCallFeature, ServiceConnectionFeature serviceConnectionFeature) {
        HashMap<Integer, Feature> features = new HashMap<>();
        features.put(R.id.video_call_menu_item, videoCallFeature);
        features.put(R.id.server_connection_menu_item, serviceConnectionFeature);

        return new Features(features);
    }

}
