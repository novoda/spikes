package com.novoda.bonfire.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsLogger {

    private static final String ACTIVITY_STARTED = "activity_started";
    private static final String ACTIVITY_STOPPED = "activity_stopped";

    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsLogger(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void trackActivityStart(Activity activity) {
        Bundle bundle = new Bundle(1);
        bundle.putString(FirebaseAnalytics.Param.VALUE, activity.getLocalClassName());
        firebaseAnalytics.logEvent(ACTIVITY_STARTED, bundle);
    }

    public void trackActivityStop(Activity activity) {
        Bundle bundle = new Bundle(1);
        bundle.putString(FirebaseAnalytics.Param.VALUE, activity.getLocalClassName());
        firebaseAnalytics.logEvent(ACTIVITY_STOPPED, bundle);
    }
}
