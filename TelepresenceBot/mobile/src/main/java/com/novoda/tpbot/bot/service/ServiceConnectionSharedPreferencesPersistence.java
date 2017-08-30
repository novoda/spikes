package com.novoda.tpbot.bot.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.novoda.tpbot.FeatureSelectionPersistence;

public final class ServiceConnectionSharedPreferencesPersistence implements FeatureSelectionPersistence {

    private static final String SERVER_CONNECTION_PREF_NAME = "server_connection";
    private static final String SERVER_CONNECTION_PREFERENCES_ON_OFF_KEY = "server_connection_preferences_on_off";
    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;

    public static ServiceConnectionSharedPreferencesPersistence newInstance(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SERVER_CONNECTION_PREF_NAME, Context.MODE_PRIVATE);
        return new ServiceConnectionSharedPreferencesPersistence(sharedPreferences);
    }

    private ServiceConnectionSharedPreferencesPersistence(SharedPreferences sharedPreferences) {
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
