/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.sync.devauth.client;

import android.content.SharedPreferences;

import com.amazonaws.cognito.sync.demo.Cognito;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This utility class is used to store content in Android's Shared Preferences.
 * For maximum security the preferences should be private.
 */
public class SharedPreferencesWrapper {

    private static final String AWS_DEVICE_UID = "AWS_DEVICE_UID";
    private static final String AWS_DEVICE_KEY = "AWS_DEVICE_KEY";
    private static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    private static final String USER_NAME      = "USER_NAME";

    public static void wipe(SharedPreferences preferences) {
        storeValue(preferences, AWS_DEVICE_KEY, null);
        storeValue(preferences, FIREBASE_TOKEN, null);

        Cognito.INSTANCE.credentialsProvider().clearCredentials();
        FirebaseAuth.getInstance().signOut();
    }

    public static void registerDevice(SharedPreferences preferences, String uid) {
        storeValue(preferences, AWS_DEVICE_UID, uid);
    }

    public static void registerFirebaseToken(SharedPreferences preferences, String token) {
        storeValue(preferences, FIREBASE_TOKEN, token);
    }

    public static String getFirebaseTokenForDevice(SharedPreferences preferences) {
        return getValue(preferences, FIREBASE_TOKEN);
    }

    public static String getUidForDevice(SharedPreferences preferences) {
        return getValue(preferences, AWS_DEVICE_UID);
    }

    public static String getKeyForDevice(SharedPreferences preferences) {
        return getValue(preferences, AWS_DEVICE_KEY);
    }

    private static void storeValue(SharedPreferences preferences, String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getValue(SharedPreferences preferences, String key) {
        return preferences.getString(key, null);
    }

    public static void registerUser(final SharedPreferences preferences, final String name, final String deviceKey) {
        storeValue(preferences, USER_NAME, name);
        storeValue(preferences, AWS_DEVICE_KEY, deviceKey);
    }

    public static String getUserName(final SharedPreferences preferences) {
        return getValue(preferences, USER_NAME);
    }
}
