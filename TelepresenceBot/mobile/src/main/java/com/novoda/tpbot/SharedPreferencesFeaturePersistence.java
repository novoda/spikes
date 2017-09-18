package com.novoda.tpbot;

import android.content.SharedPreferences;

final class SharedPreferencesFeaturePersistence implements FeaturePersistence {

    private static final boolean ON = true;
    private static final boolean OFF = false;

    private final SharedPreferences sharedPreferences;
    private final String preferenceName;

    SharedPreferencesFeaturePersistence(SharedPreferences sharedPreferences, String preferenceName) {
        this.sharedPreferences = sharedPreferences;
        this.preferenceName = preferenceName;
    }

    @Override
    public boolean isEnabled() {
        return sharedPreferences.getBoolean(preferenceName, OFF);
    }

    @Override
    public void setEnabled() {
        sharedPreferences.edit()
                .putBoolean(preferenceName, ON)
                .apply();
    }

    @Override
    public void setDisabled() {
        sharedPreferences.edit()
                .putBoolean(preferenceName, OFF)
                .apply();
    }

}
