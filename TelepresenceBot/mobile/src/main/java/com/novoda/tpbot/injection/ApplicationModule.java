package com.novoda.tpbot.injection;

import android.app.Application;
import android.content.Context;

import com.novoda.tpbot.human.HumanSubcomponent;
import com.novoda.tpbot.landing.LandingSubcomponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {LandingSubcomponent.class, HumanSubcomponent.class})
public class ApplicationModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

}
