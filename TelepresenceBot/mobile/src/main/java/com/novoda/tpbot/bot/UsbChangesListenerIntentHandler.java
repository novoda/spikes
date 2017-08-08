package com.novoda.tpbot.bot;

import android.content.Intent;
import android.hardware.usb.UsbManager;

import com.novoda.notils.exception.DeveloperError;

import java.util.HashMap;
import java.util.Map;

import static com.novoda.tpbot.bot.AndroidMovementService.ACTION_USB_PERMISSION;

class UsbChangesListenerIntentHandler {

    private static final Map<String, IntentHandler> actionMap = new HashMap<>();

    static {
        actionMap.put(ACTION_USB_PERMISSION, new IntentHandler() {
            @Override
            public void handle(Intent intent, UsbChangesListener usbChangesListener) {
                boolean permissionGranted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (permissionGranted) {
                    usbChangesListener.onPermissionGranted();
                } else {
                    usbChangesListener.onPermissionDenied();
                }
            }
        });

        actionMap.put(UsbManager.ACTION_USB_DEVICE_ATTACHED, new IntentHandler() {
            @Override
            public void handle(Intent intent, UsbChangesListener usbChangesListener) {
                usbChangesListener.onDeviceAttached();
            }
        });

        actionMap.put(UsbManager.ACTION_USB_DEVICE_DETACHED, new IntentHandler() {
            @Override
            public void handle(Intent intent, UsbChangesListener usbChangesListener) {
                usbChangesListener.onDeviceDetached();
            }
        });
    }

    static IntentHandler get(String action) {
        if (actionMap.containsKey(action)) {
            return actionMap.get(action);
        } else {
            throw new DeveloperError("No associated " + IntentHandler.class.getSimpleName() + " for action: " + action);
        }
    }

    interface IntentHandler {
        void handle(Intent intent, UsbChangesListener usbChangesListener);
    }

}
