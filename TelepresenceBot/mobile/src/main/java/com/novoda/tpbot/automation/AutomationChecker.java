package com.novoda.tpbot.automation;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class AutomationChecker {

    private final String serviceName;
    private final ContentResolver contentResolver;

    public static AutomationChecker newInstance(Context context) {
        String serviceFullyQualifiedName = context.getPackageName() + "/" + HangoutJoinerAutomationService.class.getCanonicalName();
        return new AutomationChecker(serviceFullyQualifiedName, context.getContentResolver());
    }

    private AutomationChecker(String serviceName, ContentResolver contentResolver) {
        this.serviceName = serviceName;
        this.contentResolver = contentResolver;
    }

    public boolean isHangoutJoinerAutomationServiceEnabled() {
        if (accessibilityEnabled()) {
            String[] enabledAccessibilityServices = getEnabledAccessibilityServices();

            for (String service : enabledAccessibilityServices) {
                if (matchesAutomationService(service)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean accessibilityEnabled() {
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

    private boolean matchesAutomationService(String serviceToCheck) {
        return serviceName.equalsIgnoreCase(serviceToCheck);
    }

}
