package com.gertherb.base;

import android.app.Application;

import com.gertherb.BuildConfig;
import com.novoda.notils.logger.analyse.StrictModeManager;
import com.novoda.notils.logger.simple.Log;
import com.novoda.rx.core.ObservableVault;

public class GertHerbApplication extends Application {

    private final ObservableVault observableVault = new ObservableVault();

    public ObservableVault getObservableVault() {
        return observableVault;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.SHOW_LOGS = BuildConfig.DEBUG;
        StrictModeManager.ON = BuildConfig.DEBUG;
        StrictModeManager.initializeStrictMode();
    }

}
