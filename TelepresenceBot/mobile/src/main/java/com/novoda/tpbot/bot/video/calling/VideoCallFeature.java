package com.novoda.tpbot.bot.video.calling;

import android.content.SharedPreferences;

import com.novoda.tpbot.Feature;

public final class VideoCallFeature implements Feature {

    private static final String VIDEO_CALL_PREFERENCES_ON_OFF_KEY = "video_call_preferences_on_off";
    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;

    public VideoCallFeature(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean isEnabled() {
        return sharedPreferences.getBoolean(VIDEO_CALL_PREFERENCES_ON_OFF_KEY, OFF);
    }

    @Override
    public void setEnabled() {
        sharedPreferences.edit()
                .putBoolean(VIDEO_CALL_PREFERENCES_ON_OFF_KEY, ON)
                .apply();
    }

    @Override
    public void setDisabled() {
        sharedPreferences.edit()
                .putBoolean(VIDEO_CALL_PREFERENCES_ON_OFF_KEY, OFF)
                .apply();
    }

}
