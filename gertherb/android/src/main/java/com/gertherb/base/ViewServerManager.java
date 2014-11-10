package com.gertherb.base;

import android.app.Activity;

import com.gertherb.BuildConfig;

public class ViewServerManager {

    private Activity activity;

    public ViewServerManager(Activity activity) {
        this.activity = activity;
    }

    public void onCreate() {
        if (BuildConfig.DEBUG) {
            ViewServer.get(activity).addWindow(activity);
        }
    }

    public void onResume() {
        if (BuildConfig.DEBUG) {
            ViewServer.get(activity).setFocusedWindow(activity);
        }
    }

    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            ViewServer.get(activity).removeWindow(activity);
        }
    }

}
