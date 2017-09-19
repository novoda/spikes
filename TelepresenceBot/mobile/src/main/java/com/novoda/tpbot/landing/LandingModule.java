package com.novoda.tpbot.landing;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.tpbot.FeaturePersistence;
import com.novoda.tpbot.FeaturePersistenceFactory;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.Features;
import com.novoda.tpbot.R;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;

@Module
public class LandingModule {

    @Provides
    Features provideFeatures(FeaturePersistenceFactory featurePersistenceFactory) {
        Map<Integer, FeaturePersistence> features = new HashMap<>();
        features.put(R.id.video_call_menu_item, featurePersistenceFactory.createVideoCallPersistence());
        features.put(R.id.server_connection_menu_item, featurePersistenceFactory.createServiceConnectionPersistence());

        return new Features(features);
    }

    @Provides
    LandingView provideLandingView(LandingActivity landingActivity) {
        return landingActivity;
    }

    @Provides
    FeatureSelectionController<Menu, MenuItem> provideController(MenuInflater menuInflater, Features features) {
        return new LandingMenuFeatureSelectionController(menuInflater, features);
    }

    @Provides
    Navigator provideNavigator(LandingActivity activity) {
        return new IntentNavigator(activity);
    }

    @Provides
    LandingPresenter provideLandingPresenter(LandingView landingView, Navigator navigator) {
        return new LandingPresenter(landingView, navigator);
    }

}
