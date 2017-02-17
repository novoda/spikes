package com.amazonaws.cognito.sync.demo;

import android.content.SharedPreferences;

import com.amazonaws.cognito.sync.devauth.client.SharedPreferencesWrapper;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public class Identifiers {

    private final SharedPreferences preferences;

    public Identifiers(SharedPreferences preferences) {
        this.preferences = preferences;
        registerDevice(generateUniqueDeviceId());
    }

    private static String generateUniqueDeviceId() {
        byte[] randomBytes = new SecureRandom().generateSeed(16);
        return new String(Hex.encodeHex(randomBytes));
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

    private void registerDevice(final String uniqueDeviceId) {
        SharedPreferencesWrapper.registerDevice(preferences, uniqueDeviceId);
    }

    public void wipe() {
        SharedPreferencesWrapper.wipe(preferences);
    }

    public String getUserName() {
        return SharedPreferencesWrapper.getUserName(preferences);
    }
}
