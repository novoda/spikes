package com.novoda.tpbot.automation;

import android.content.Context;

public class AutomationChecker {

    private final String serviceName;
    private final AndroidAccessibilitySettingsRetriever accessibilitySettingsRetriever;
    private final ColonStringSplitter colonStringSplitter;

    public static AutomationChecker newInstance(Context context) {
        String serviceFullyQualifiedName = context.getPackageName() + "/" + HangoutJoinerAutomationService.class.getCanonicalName();
        AndroidAccessibilitySettingsRetriever retriever = new AndroidAccessibilitySettingsRetriever(context.getContentResolver());
        ColonStringSplitter colonStringSplitter = new ColonStringSplitter();
        return new AutomationChecker(retriever, colonStringSplitter, serviceFullyQualifiedName);
    }

    AutomationChecker(AndroidAccessibilitySettingsRetriever accessibilitySettingsRetriever,
                      ColonStringSplitter colonStringSplitter,
                      String serviceName) {
        this.serviceName = serviceName;
        this.accessibilitySettingsRetriever = accessibilitySettingsRetriever;
        this.colonStringSplitter = colonStringSplitter;
    }

    public boolean isHangoutJoinerAutomationServiceEnabled() {
        String accessibilitySettings = accessibilitySettingsRetriever.retrieveEnabledAccessibilityServices();
        String[] accessibilityServices = colonStringSplitter.split(accessibilitySettings);

        for (String service : accessibilityServices) {
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
