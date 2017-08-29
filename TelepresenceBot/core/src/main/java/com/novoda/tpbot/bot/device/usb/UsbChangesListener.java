package com.novoda.tpbot.bot.device.usb;

interface UsbChangesListener {

    void onPermissionGranted();

    void onPermissionDenied();

    void onDeviceAttached();

    void onDeviceDetached();
}
