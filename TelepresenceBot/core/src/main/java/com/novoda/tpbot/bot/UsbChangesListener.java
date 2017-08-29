package com.novoda.tpbot.bot;

public interface UsbChangesListener {

    void onPermissionGranted();

    void onPermissionDenied();

    void onDeviceAttached();

    void onDeviceDetached();
}
