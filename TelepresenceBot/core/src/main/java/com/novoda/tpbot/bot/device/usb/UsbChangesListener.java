package com.novoda.tpbot.bot.device.usb;

public interface UsbChangesListener {

    void onPermissionGranted();

    void onPermissionDenied();

    void onDeviceAttached();

    void onDeviceDetached();
}
