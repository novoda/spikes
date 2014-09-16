package com.novoda.geofencing;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PersistentLogger {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

    private static final String NAME = BuildConfig.PACKAGE_NAME + "_LOG";
    private static final String LOG_MESSAGES_KEY = "LOG_MESSAGES";

    private final SharedPreferences preferences;

    public static PersistentLogger newInstance(Context context) {
        return new PersistentLogger(context.getSharedPreferences(NAME, Context.MODE_APPEND));
    }

    public PersistentLogger(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void logDebug(String tag, String message) {
        Log.d(tag, message);
        persistLog("D/" + tag, message);
    }

    public void logError(String tag, String message) {
        Log.e(tag, message);
        persistLog("E/" + tag, message);
    }

    public void logError(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
        persistLog("E/" + tag, message + "\n" + Log.getStackTraceString(throwable));
    }

    public String readLogs() {
        return preferences.getString(LOG_MESSAGES_KEY, "");
    }

    private void persistLog(String tag, String message) {
        String formattedMessage = format.format(new Date()) + "|" + tag + ": " + message + "\n";
        preferences.edit().putString(LOG_MESSAGES_KEY, readLogs() + formattedMessage).apply();
    }
}
