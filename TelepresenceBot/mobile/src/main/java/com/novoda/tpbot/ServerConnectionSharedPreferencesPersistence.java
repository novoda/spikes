package com.novoda.tpbot;

import android.content.Context;
import android.content.SharedPreferences;

final class ServerConnectionSharedPreferencesPersistence implements FeatureSelectionPersistence {

    private static final String SERVER_CONNECTION_PREF_NAME = "server_connection";
    private static final String SERVER_CONNECTION_PREFERENCES_ON_OFF_KEY = "server_connection_preferences_on_off";
    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;

    static ServerConnectionSharedPreferencesPersistence newInstance(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SERVER_CONNECTION_PREF_NAME, Context.MODE_PRIVATE);
        return new ServerConnectionSharedPreferencesPersistence(sharedPreferences);
    }

    private ServerConnectionSharedPreferencesPersistence(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean isFeatureEnabled() {
        return sharedPreferences.getBoolean(SERVER_CONNECTION_PREFERENCES_ON_OFF_KEY, OFF);
    }

    @Override
    public void setFeatureEnabled() {
        sharedPreferences.edit()
                .putBoolean(SERVER_CONNECTION_PREFERENCES_ON_OFF_KEY, ON)
                .apply();
    }

    @Override
    public void setFeatureDisabled() {
        sharedPreferences.edit()
                .putBoolean(SERVER_CONNECTION_PREFERENCES_ON_OFF_KEY, OFF)
                .apply();
    }

}
