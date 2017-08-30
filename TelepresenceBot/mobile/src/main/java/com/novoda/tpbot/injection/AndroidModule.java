package com.novoda.tpbot.injection;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.landing.menu.LandingMenuFeatureSelectionController;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    private final Context context;

    public AndroidModule(Context context) {
        this.context = context;
    }

    @Provides
    FeatureSelectionController<Menu, MenuItem> providesLandingSelectionController() {
        return LandingMenuFeatureSelectionController.createFrom(context);
    }

}
