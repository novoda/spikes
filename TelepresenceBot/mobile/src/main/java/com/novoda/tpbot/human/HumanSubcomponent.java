package com.novoda.tpbot.human;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = HumanModule.class)
public interface HumanSubcomponent extends AndroidInjector<HumanActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<HumanActivity> {
    }

}
