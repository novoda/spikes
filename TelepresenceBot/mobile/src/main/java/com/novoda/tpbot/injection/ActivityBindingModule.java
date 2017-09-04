package com.novoda.tpbot.injection;

import android.app.Activity;

import com.novoda.tpbot.landing.LandingActivity;
import com.novoda.tpbot.landing.LandingActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class ActivityBindingModule {

    @Binds
    @IntoMap
    @ActivityKey(LandingActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindLandingActivity(LandingActivitySubcomponent.Builder builder);

}
