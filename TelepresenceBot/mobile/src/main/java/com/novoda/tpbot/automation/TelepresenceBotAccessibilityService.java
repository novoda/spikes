package com.novoda.tpbot.automation;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class TelepresenceBotAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TelepresenceBotAccessibilityService.class.getSimpleName(), event.toString());
    }

    @Override
    public void onInterrupt() {
        Log.e(TelepresenceBotAccessibilityService.class.getSimpleName(), "onInterrupt");
    }

}
