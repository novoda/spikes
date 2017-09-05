package com.novoda.tpbot.landing;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = LandingActivityModule.class)
public interface LandingActivitySubcomponent extends AndroidInjector<LandingActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<LandingActivity> {
    }

}
