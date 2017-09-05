package com.novoda.tpbot.injection;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.novoda.tpbot.controls.LastServerPersistence;
import com.novoda.tpbot.controls.LastServerPreferences;
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

    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    LastServerPersistence provideLastServerPersistence(SharedPreferences sharedPreferences) {
        return new LastServerPreferences(sharedPreferences);
    }

}
