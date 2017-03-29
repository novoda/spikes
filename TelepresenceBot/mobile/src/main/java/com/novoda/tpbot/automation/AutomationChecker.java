package com.novoda.tpbot.automation;

public class AutomationChecker {

    private final String serviceName;
    private final AndroidAccessibilitySettingsRetriever accessibilitySettingsRetriever;
    private final ColonStringSplitter colonStringSplitter;

    public AutomationChecker(AndroidAccessibilitySettingsRetriever accessibilitySettingsRetriever,
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
