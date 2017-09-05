package com.novoda.tpbot.landing;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = LandingModule.class)
public interface LandingSubcomponent extends AndroidInjector<LandingActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<LandingActivity> {
    }

}
