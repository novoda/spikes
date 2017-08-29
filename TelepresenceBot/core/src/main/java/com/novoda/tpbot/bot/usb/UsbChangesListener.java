package com.novoda.tpbot.bot.usb;

public interface UsbChangesListener {

    void onPermissionGranted();

    void onPermissionDenied();

    void onDeviceAttached();

    void onDeviceDetached();
}
