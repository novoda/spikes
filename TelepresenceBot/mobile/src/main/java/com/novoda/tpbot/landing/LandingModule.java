package com.novoda.tpbot.landing;

import android.content.Context;
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

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class LandingModule {

    @Binds
    abstract LandingView provideLandingView(LandingActivity landingActivity);

    @Provides
    static Features provideFeatures(FeaturePersistenceFactory featurePersistenceFactory) {
        Map<Integer, FeaturePersistence> features = new HashMap<>();
        features.put(R.id.video_call_menu_item, featurePersistenceFactory.createVideoCallPersistence());
        features.put(R.id.server_connection_menu_item, featurePersistenceFactory.createServiceConnectionPersistence());

        return new Features(features);
    }

    @Provides
    static FeatureSelectionController<Menu, MenuItem> provideController(Context context, Features features) {
        MenuInflater menuInflater = new MenuInflater(context);
        return new LandingMenuFeatureSelectionController(menuInflater, features);
    }

    @Provides
    static Navigator provideNavigator(LandingActivity activity) {
        return new IntentNavigator(activity);
    }

    @Provides
    static LandingPresenter provideLandingPresenter(LandingView landingView, Navigator navigator) {
        return new LandingPresenter(landingView, navigator);
    }

}
