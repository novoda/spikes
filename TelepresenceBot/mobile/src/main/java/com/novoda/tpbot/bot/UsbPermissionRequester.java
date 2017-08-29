package com.novoda.tpbot.bot;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import static com.novoda.tpbot.bot.UsbChangesRegister.ACTION_USB_PERMISSION;

class UsbPermissionRequester {

    private static final int REQUEST_CODE = 0;
    private static final int NO_FLAGS = 0;

    private final Context context;
    private final UsbManager usbManager;

    UsbPermissionRequester(Context context, UsbManager usbManager) {
        this.context = context;
        this.usbManager = usbManager;
    }

    void requestPermission(UsbDevice usbDevice) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(ACTION_USB_PERMISSION), NO_FLAGS);
        usbManager.requestPermission(usbDevice, pendingIntent);
    }

}
