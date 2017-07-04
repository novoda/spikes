package com.example;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.androidskeleton.BuildConfig;

class ResumePoints {

    private static final String FILE_NAME = BuildConfig.APPLICATION_ID + ".ResumePoints";
    private static final String KEY_RESUME_POINT = "resume_point";
    private static final String KEY_WAS_PLAYING = "was_playing";

    private final SharedPreferences sharedPreferences;

    static ResumePoints create(Context context) {
        return new ResumePoints(context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE));
    }

    private ResumePoints(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public long getResumePoint() {
        return sharedPreferences.getLong(KEY_RESUME_POINT, 0);
    }

    public void setResumePoint(long millis) {
        sharedPreferences.edit().putLong(KEY_RESUME_POINT, millis).apply();
    }

    public boolean getWasPlaying() {
        return sharedPreferences.getBoolean(KEY_WAS_PLAYING, false);
    }

    public void setWasPlaying(boolean wasPlaying) {
        sharedPreferences.edit().putBoolean(KEY_WAS_PLAYING, wasPlaying).apply();
    }

}
