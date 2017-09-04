package com.novoda.tpbot;

import android.app.Activity;
import android.app.Application;

import com.novoda.notils.logger.simple.Log;
import com.novoda.tpbot.injection.DaggerTelepresenceBotApplicationComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class TelepresenceBotApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setShowLogs(BuildConfig.DEBUG);

        DaggerTelepresenceBotApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

}
