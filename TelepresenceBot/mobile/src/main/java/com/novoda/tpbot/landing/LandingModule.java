package com.novoda.tpbot.landing;

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
import java.util.Map;

import dagger.Module;
import dagger.Provides;

@Module
public class LandingModule {

    @Provides
    Features provideFeatures(VideoCallFeature videoCallFeature, ServiceConnectionFeature serviceConnectionFeature) {
        Map<Integer, Feature> features = new HashMap<>();
        features.put(R.id.video_call_menu_item, videoCallFeature);
        features.put(R.id.server_connection_menu_item, serviceConnectionFeature);

        return new Features(features);
    }

    @Provides
    FeatureSelectionController<Menu, MenuItem> provideController(MenuInflater menuInflater, Features features) {
        return new LandingMenuFeatureSelectionController(menuInflater, features);
    }

}
