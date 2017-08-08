package com.novoda.tpbot.bot;

interface UsbChangesListener {

    void onPermissionGranted();

    void onPermissionDenied();

    void onDeviceAttached();

    void onDeviceDetached();

    void onBotConnectivityChanged(BotConnectivityChangeEvent botConnectivityChangeEvent);
}
