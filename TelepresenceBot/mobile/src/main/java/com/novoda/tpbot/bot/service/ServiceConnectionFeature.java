package com.novoda.tpbot.bot.service;

import android.content.SharedPreferences;

import com.novoda.tpbot.FeatureSelection;

// TODO: Could have Feature<T extends something> where T is the persistence or the concrete implementation. This class then just delegates to the persistence layer.
public final class ServiceConnectionFeature implements FeatureSelection {

    private static final String SERVER_CONNECTION_PREFERENCES_ON_OFF_KEY = "server_connection_preferences_on_off";
    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;

    public ServiceConnectionFeature(SharedPreferences sharedPreferences) {
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
