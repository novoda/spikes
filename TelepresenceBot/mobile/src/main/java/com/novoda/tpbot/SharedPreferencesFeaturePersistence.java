package com.novoda.tpbot;

import android.content.SharedPreferences;

final class SharedPreferencesFeaturePersistence {

    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;
    private final String preferenceName;

    SharedPreferencesFeaturePersistence(SharedPreferences sharedPreferences, String preferenceName) {
        this.sharedPreferences = sharedPreferences;
        this.preferenceName = preferenceName;
    }

    public boolean isEnabled() {
        return sharedPreferences.getBoolean(preferenceName, OFF);
    }

    public void setEnabled() {
        sharedPreferences.edit()
                .putBoolean(preferenceName, ON)
                .apply();
    }

    public void setDisabled() {
        sharedPreferences.edit()
                .putBoolean(preferenceName, OFF)
                .apply();
    }

}
