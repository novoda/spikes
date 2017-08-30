package com.novoda.tpbot.injection;

import com.novoda.tpbot.landing.LandingActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract LandingActivity contributeActivityInjector();

}
