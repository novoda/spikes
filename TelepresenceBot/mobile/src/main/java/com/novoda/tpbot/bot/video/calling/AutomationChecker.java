package com.novoda.tpbot.bot.video.calling;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class AutomationChecker {

    private static final String SERVICE_ID = "com.novoda.tpbot/.automation.HangoutJoinerAutomationService";

    private final AccessibilityManager accessibilityManager;

    public AutomationChecker(AccessibilityManager accessibilityManager) {
        this.accessibilityManager = accessibilityManager;
    }

    public boolean isHangoutJoinerAutomationServiceEnabled() {
        List<AccessibilityServiceInfo> visualFeedbackServices = getVisualFeedbackServices();

        for (AccessibilityServiceInfo service : visualFeedbackServices) {
            if (SERVICE_ID.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }

    private List<AccessibilityServiceInfo> getVisualFeedbackServices() {
        return accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_VISUAL
        );
    }

}
