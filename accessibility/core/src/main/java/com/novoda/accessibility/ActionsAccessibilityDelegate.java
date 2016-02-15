package com.novoda.accessibility;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;

public class ActionsAccessibilityDelegate extends AccessibilityDelegateCompat {

    private final Resources resources;
    private final Actions actions;

    public ActionsAccessibilityDelegate(Resources resources, Actions actions) {
        this.resources = resources;
        this.actions = actions;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        for (Action action : actions) {
            String label = resources.getString(action.getLabel());
            info.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(action.getId(), label));
        }
    }

    @Override
    public boolean performAccessibilityAction(View host, int actionId, Bundle args) {
        Action action = actions.findActionById(actionId);
        if (action == null) {
            return super.performAccessibilityAction(host, actionId, args);
        } else {
            action.run();
            return true;
        }
    }

}
