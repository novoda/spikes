package com.novoda.tpbot.injection;

import android.app.Application;
import android.view.MenuInflater;

import com.novoda.tpbot.landing.LandingActivitySubcomponent;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {LandingActivitySubcomponent.class})
public class ApplicationModule {

    @Provides
    MenuInflater provideMenuInflater(Application application) {
        return new MenuInflater(application.getApplicationContext());
    }

}
