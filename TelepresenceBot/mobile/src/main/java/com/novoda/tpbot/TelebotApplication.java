package com.novoda.tpbot;

import android.app.Application;

import com.novoda.notils.logger.simple.Log;

public class TelebotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setShowLogs(BuildConfig.DEBUG);
    }
}
