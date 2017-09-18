package com.novoda.tpbot;

import android.content.Context;
import android.content.SharedPreferences;

public class FeaturePersistenceFactory {

    private final Context context;

    public FeaturePersistenceFactory(Context context) {
        this.context = context;
    }

    FeaturePersistence createServiceConnectionPersistence() {
        String serverConnectionPreferenceName = "server_connection";
        SharedPreferences sharedPreferences = context.getSharedPreferences(serverConnectionPreferenceName, Context.MODE_PRIVATE);
        return new FeaturePersistence(sharedPreferences, serverConnectionPreferenceName);
    }

    FeaturePersistence createVideoCallPersistence() {
        String videoCallPreferenceName = "video_call";
        SharedPreferences sharedPreferences = context.getSharedPreferences(videoCallPreferenceName, Context.MODE_PRIVATE);
        return new FeaturePersistence(sharedPreferences, videoCallPreferenceName);
    }

}


