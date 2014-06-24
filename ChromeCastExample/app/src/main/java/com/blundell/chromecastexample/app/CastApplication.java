package com.blundell.chromecastexample.app;

import android.app.Application;
import android.content.Context;

import com.google.sample.castcompanionlibrary.cast.VideoCastManager;

public class CastApplication extends Application {

    private static VideoCastManager mCastMgr = null;
    public static final double VOLUME_INCREMENT = 0.05;

    public static VideoCastManager getCastManager(Context context) {
        if (null == mCastMgr) {
            mCastMgr = VideoCastManager.initialize(context, BuildConfig.CAST_APP_ID, null, null);
            mCastMgr.enableFeatures(VideoCastManager.FEATURE_NOTIFICATION
                    | VideoCastManager.FEATURE_LOCKSCREEN
                    | VideoCastManager.FEATURE_DEBUGGING);
        }
        mCastMgr.setContext(context);
        mCastMgr.setStopOnDisconnect(true);
        return mCastMgr;
    }
}
