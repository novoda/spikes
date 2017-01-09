package com.novoda.tpbot.bot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

public class UsbReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent movementService = new Intent(context.getApplicationContext(), MovementService.class);
        if (usbConnected(intent)) {
            context.startService(movementService);
        } else if (usbDisconnected(intent)) {
            context.stopService(movementService);
        }
    }

    private boolean usbConnected(Intent intent) {
        return UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction());
    }

    private boolean usbDisconnected(Intent intent) {
        return UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction());
    }
}
