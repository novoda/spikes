package com.novoda.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public final class AccessibilityServices {

    private final AccessibilityManager accessibilityManager;

    public static AccessibilityServices newInstance(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return new AccessibilityServices(accessibilityManager);
    }

    private AccessibilityServices(AccessibilityManager accessibilityManager) {
        this.accessibilityManager = accessibilityManager;
    }

    /**
     * Reports if any services offering spoken feedback are enabled.
     *
     * Be aware it will return true when TalkBack is enabled, even if it's suspended.
     */
    public boolean isSpokenFeedbackEnabled() {
        List<AccessibilityServiceInfo> enabledServices = getEnabledServicesFor(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
        return !enabledServices.isEmpty();
    }

    private List<AccessibilityServiceInfo> getEnabledServicesFor(int feedbackTypeFlags) {
        return accessibilityManager.getEnabledAccessibilityServiceList(feedbackTypeFlags);
    }

}
