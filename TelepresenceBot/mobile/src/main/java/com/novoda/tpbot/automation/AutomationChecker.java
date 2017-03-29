package com.novoda.tpbot.automation;

import android.content.Context;

public class AutomationChecker {

    private final String serviceName;
    private final AndroidAccessibilitySettingsRetriever accessibilitySettingsRetriever;

    public static AutomationChecker newInstance(Context context) {
        String serviceFullyQualifiedName = context.getPackageName() + "/" + HangoutJoinerAutomationService.class.getCanonicalName();
        AndroidAccessibilitySettingsRetriever retriever = new AndroidAccessibilitySettingsRetriever(context.getContentResolver());
        return new AutomationChecker(serviceFullyQualifiedName, retriever);
    }

    AutomationChecker(String serviceName, AndroidAccessibilitySettingsRetriever accessibilitySettingsRetriever) {
        this.serviceName = serviceName;
        this.accessibilitySettingsRetriever = accessibilitySettingsRetriever;
    }

    public boolean isHangoutJoinerAutomationServiceEnabled() {
        String[] enabledAccessibilityServices = accessibilitySettingsRetriever.retrieveEnabledAccessibilityServices();

        for (String service : enabledAccessibilityServices) {
            if (matchesAutomationService(service)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAutomationService(String serviceToCheck) {
        return serviceName.equalsIgnoreCase(serviceToCheck);
    }

}
