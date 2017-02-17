package com.amazonaws.cognito.sync.demo;

import android.content.SharedPreferences;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public class Identifiers {

    private static final String AWS_DEVICE_UID = "AWS_DEVICE_UID";
    private static final String AWS_DEVICE_KEY = "AWS_DEVICE_KEY";
    private static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    private static final String USER_NAME = "USER_NAME";

    private final SharedPreferences preferences;

    public Identifiers(SharedPreferences preferences) {
        this.preferences = preferences;
        ensureUDId();
    }

    private void ensureUDId() {
        if (getUidForDevice() == null) {
            registerDevice(generateUniqueDeviceId());
        }
    }

    private static String generateUniqueDeviceId() {
        byte[] randomBytes = new SecureRandom().generateSeed(16);
        return new String(Hex.encodeHex(randomBytes));
    }

    public String getUidForDevice() {
        return preferences.getString(AWS_DEVICE_UID, null);
    }

    public String getKeyForDevice() {
        return preferences.getString(AWS_DEVICE_KEY, null);
    }

    public String getUserName() {
        return preferences.getString(USER_NAME, null);
    }

    public void registerFirebaseToken(final String result) {
        preferences.edit()
                .putString(FIREBASE_TOKEN, result)
                .apply();
    }

    public void registerUser(final String userName, final String deviceKey) {
        preferences.edit()
                .putString(USER_NAME, userName)
                .putString(AWS_DEVICE_KEY, deviceKey)
                .apply();
    }

    private void registerDevice(final String uniqueDeviceId) {
        preferences.edit()
                .putString(AWS_DEVICE_UID, uniqueDeviceId)
                .apply();
    }

    public void wipe() {
        preferences.edit()
                .remove(AWS_DEVICE_KEY)
                .remove(FIREBASE_TOKEN)
                .apply();
    }

}
