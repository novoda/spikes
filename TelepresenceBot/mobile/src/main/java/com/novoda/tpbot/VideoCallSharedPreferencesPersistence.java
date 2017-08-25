package com.novoda.tpbot;

import android.content.Context;
import android.content.SharedPreferences;

class VideoCallSharedPreferencesPersistence implements FeatureSelectionPersistence {

    private static final String VIDEO_CALL_PREF_NAME = "video_call";
    private static final String VIDEO_CALL_PREFERENCES_ON_OFF_KEY = "video_call_preferences_on_off";
    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;

    public static VideoCallSharedPreferencesPersistence newInstance(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(VIDEO_CALL_PREF_NAME, Context.MODE_PRIVATE);
        return new VideoCallSharedPreferencesPersistence(sharedPreferences);
    }

    VideoCallSharedPreferencesPersistence(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean isFeatureEnabled() {
        return sharedPreferences.getBoolean(VIDEO_CALL_PREFERENCES_ON_OFF_KEY, OFF);
    }

    @Override
    public void setFeatureEnabled() {
        sharedPreferences.edit()
                .putBoolean(VIDEO_CALL_PREFERENCES_ON_OFF_KEY, ON)
                .apply();
    }

    @Override
    public void setFeatureDisabled() {
        sharedPreferences.edit()
                .putBoolean(VIDEO_CALL_PREFERENCES_ON_OFF_KEY, OFF)
                .apply();
    }

}
