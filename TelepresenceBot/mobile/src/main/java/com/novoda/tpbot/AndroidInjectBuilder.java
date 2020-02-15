package com.novoda.tpbot;

import com.novoda.tpbot.injection.ApplicationModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class AndroidInjectBuilder {
    @ContributesAndroidInjector(modules = {ApplicationModule.class})
    abstract ApplicationModule bindApplicationModule();
}
