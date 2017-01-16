package com.novoda.toggletalkback;

import android.content.ContentResolver;
import android.provider.Settings;

class Toggler {

    private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";
    private static final String COLON_TALKBACK_SERVICE_NAME = ":" + TALKBACK_SERVICE_NAME;
    private static final String TALKBACK_SERVICE_NAME_COLON = TALKBACK_SERVICE_NAME + ":";
    private static final String VALUE_DISABLED = "0";
    private static final String VALUE_ENABLED = "1";
    private static final String EMPTY_STRING = "";

    private final ContentResolver contentResolver;
    private final Callback callback;

    Toggler(ContentResolver contentResolver, Callback callback) {
        this.contentResolver = contentResolver;
        this.callback = callback;
    }

    void enableTalkBack() {
        try {
            String enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (enabledServices.contains(TALKBACK_SERVICE_NAME)) {
                return;
            }

            Settings.Secure.putString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, enabledServices + COLON_TALKBACK_SERVICE_NAME);
            Settings.Secure.putString(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_ENABLED);
        } catch (SecurityException se) {
            callback.onSecurityExceptionThrown();
        }
    }

    void disableTalkBack() {
        String enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (!enabledServices.contains(TALKBACK_SERVICE_NAME)) {
            return;
        }

        String servicesWithoutTalkBack = enabledServices
                .replace(COLON_TALKBACK_SERVICE_NAME, EMPTY_STRING)
                .replace(TALKBACK_SERVICE_NAME_COLON, EMPTY_STRING)
                .replace(TALKBACK_SERVICE_NAME, EMPTY_STRING);

        Settings.Secure.putString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, servicesWithoutTalkBack);

        String value = servicesWithoutTalkBack.isEmpty() ? VALUE_DISABLED : VALUE_ENABLED;
        Settings.Secure.putString(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, value);
    }

    interface Callback {

        void onSecurityExceptionThrown();

    }

}