package com.novoda.tpbot.injection;

import android.app.Activity;

import com.novoda.tpbot.human.HumanActivity;
import com.novoda.tpbot.human.HumanSubcomponent;
import com.novoda.tpbot.landing.LandingActivity;
import com.novoda.tpbot.landing.LandingSubcomponent;

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
    abstract AndroidInjector.Factory<? extends Activity> bindLandingActivity(LandingSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(HumanActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindHumanActivity(HumanSubcomponent.Builder builder);

}
