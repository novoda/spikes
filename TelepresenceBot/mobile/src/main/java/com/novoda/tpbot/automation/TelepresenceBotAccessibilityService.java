package com.novoda.tpbot.automation;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class TelepresenceBotAccessibilityService extends AccessibilityService {

    private static final String HANGOUTS_PACKAGE_NAME = "com.google.android.talk";
    private static final String JOIN_VIDEO_CALL = "JOIN VIDEO CALL";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();

        if (source == null) {
            return;
        }

        String packageName = String.valueOf(source.getPackageName());
        int eventType = event.getEventType();

        if (isHangouts(packageName) && isWindowStateChangeEvent(eventType)) {
            List<AccessibilityNodeInfo> accessibilityNodes = source.findAccessibilityNodeInfosByText(JOIN_VIDEO_CALL);
            if (containsJoinButton(accessibilityNodes)) {
                AccessibilityNodeInfo joinCallNode = accessibilityNodes.get(0);
                joinCallNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.e(TelepresenceBotAccessibilityService.class.getSimpleName(), "Could not locate join video call button");
            }
        }
    }

    private boolean isHangouts(String packageName) {
        return packageName.equals(HANGOUTS_PACKAGE_NAME);
    }

    private boolean isWindowStateChangeEvent(int eventType) {
        return eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
    }

    private boolean containsJoinButton(List<AccessibilityNodeInfo> accessibilityNodes) {
        return accessibilityNodes.size() > 0;
    }

    @Override
    public void onInterrupt() {
        Log.e(TelepresenceBotAccessibilityService.class.getSimpleName(), "onInterrupt");
    }

}
