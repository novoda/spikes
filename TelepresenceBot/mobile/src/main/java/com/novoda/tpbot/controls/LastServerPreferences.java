package com.novoda.tpbot.controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LastServerPreferences implements LastServerPersistence {

    private static final String KEY_LAST_SERVER = "last_server";

    private final SharedPreferences preferences;

    public static LastServerPreferences newInstance(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new LastServerPreferences(sharedPreferences);
    }

    LastServerPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void saveLastConnectedServer(String serverAddress) {
        preferences.edit()
                .putString(KEY_LAST_SERVER, serverAddress)
                .apply();
    }

    @Override
    public boolean containsLastConnectedServer() {
        return preferences.contains(KEY_LAST_SERVER);
    }

    @Override
    public String getLastConnectedServer() {
        return preferences.getString(KEY_LAST_SERVER, "");
    }
}
