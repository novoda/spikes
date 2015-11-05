package com.novoda.easycustomtabs.demo;

import android.app.Application;

import com.novoda.easycustomtabs.EasyCustomTabs;

public class ExtendedDemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyCustomTabs.initialize(this);
    }
}
