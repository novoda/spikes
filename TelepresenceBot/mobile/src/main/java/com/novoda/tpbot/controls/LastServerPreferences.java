package com.novoda.tpbot.controls;

import android.content.SharedPreferences;

public class LastServerPreferences {

    private static final String KEY_LAST_SERVER = "last_server";

    private final SharedPreferences preferences;

    public LastServerPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void saveLastConnectedServer(String serverAddress) {
        preferences.edit()
                .putString(KEY_LAST_SERVER, serverAddress)
                .apply();
    }

    public boolean containsLastConnectedServer() {
        return preferences.contains(KEY_LAST_SERVER);
    }

    public String getLastConnectedServer() {
        return preferences.getString(KEY_LAST_SERVER, "");
    }
}
