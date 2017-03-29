package com.novoda.tpbot.automation;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

public class AndroidAccessibilitySettingsRetriever {

    private final ContentResolver contentResolver;

    public AndroidAccessibilitySettingsRetriever(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public String[] retrieveEnabledAccessibilityServices() {
        if (canRetrieveAccessibilitySettings()) {
            return getEnabledAccessibilityServices();
        }
        return new String[]{};
    }

    private boolean canRetrieveAccessibilitySettings() {
        try {
            return Settings.Secure.getInt(
                    contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
            ) != 0;
        } catch (Settings.SettingNotFoundException e) {
            Log.e(AutomationChecker.class.getSimpleName(), "Could not find settings.", e);
            return false;
        }
    }

    private String[] getEnabledAccessibilityServices() {
        String accessibilityServices = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );

        if (accessibilityServices == null) {
            return new String[]{};
        } else {
            return accessibilityServices.split(":");
        }
    }
}
