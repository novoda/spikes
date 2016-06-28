package net.bonysoft.magicmirror;

import android.app.Application;

import com.novoda.notils.logger.simple.Log;

import net.danlew.android.joda.JodaTimeAndroid;

public class MagicMirrorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setShowLogs(BuildConfig.DEBUG);
        JodaTimeAndroid.init(this);
    }

}
