package com.novoda.tpbot.automation;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

public class AndroidAccessibilitySettingsRetriever {

    private static final String EMPTY_STRING = "";

    private final ContentResolver contentResolver;

    public AndroidAccessibilitySettingsRetriever(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public String retrieveEnabledAccessibilityServices() {
        if (canRetrieveAccessibilitySettings()) {
            return getEnabledAccessibilityServices();
        }
        return EMPTY_STRING;
    }

    private boolean canRetrieveAccessibilitySettings() {
        try {
            return Settings.Secure.getInt(
                    contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 1;
        } catch (Settings.SettingNotFoundException e) {
            Log.e(AutomationChecker.class.getSimpleName(), "Could not find settings.", e);
            return false;
        }
    }

    private String getEnabledAccessibilityServices() {
        return Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
    }
}
