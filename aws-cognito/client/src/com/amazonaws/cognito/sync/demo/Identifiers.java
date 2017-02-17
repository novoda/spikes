package com.amazonaws.cognito.sync.demo;

import android.content.SharedPreferences;

import com.amazonaws.cognito.sync.devauth.client.SharedPreferencesWrapper;

public class Identifiers {

    private final SharedPreferences preferences;

    public Identifiers(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void registerFirebaseToken(final String result) {
        SharedPreferencesWrapper.registerFirebaseToken(preferences, result);
    }

    public String getUidForDevice() {
        return SharedPreferencesWrapper.getUidForDevice(preferences);
    }

    public String getKeyForDevice() {
        return SharedPreferencesWrapper.getKeyForDevice(preferences);
    }

    public void registerUser(final String userName, final String deviceKey) {
        SharedPreferencesWrapper.registerUser(preferences, userName, deviceKey);
    }

    public void registerDevice(final String uniqueDeviceId) {
        SharedPreferencesWrapper.registerDevice(preferences, uniqueDeviceId);
    }

    public void wipe() {
        SharedPreferencesWrapper.wipe(preferences);
    }

    public String getUserName() {
        return SharedPreferencesWrapper.getUserName(preferences);
    }
}
