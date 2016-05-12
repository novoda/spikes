package com.novoda.bonfire.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.google.firebase.analytics.FirebaseAnalytics.Param.VALUE;

public class FirebaseAnalyticsAnalytics implements Analytics {

    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsAnalytics(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void trackEvent(String eventName, Object... args) {
        Bundle bundle = new Bundle();
        bundle.putString(VALUE, argumentsAsString(args));
        firebaseAnalytics.logEvent(eventName, bundle);
    }

    private String argumentsAsString(Object[] args) {
        StringBuilder builder = new StringBuilder();

        int length = args.length;
        for (int i = 0; i < length; i++) {
            Object arg = args[i];
            builder.append(arg);
            if (i < length - 1) {
                builder.append(" : ");
            }

        }
        return builder.toString();
    }
}
