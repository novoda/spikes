package com.novoda.tpbot.bot;

interface MovementService {

    void onPermissionGranted();

    void onPermissionDenied();

    void onDeviceAttached();

    void onDeviceDetached();

}
