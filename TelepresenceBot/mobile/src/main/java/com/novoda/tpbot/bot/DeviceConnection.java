package com.novoda.tpbot.bot;

abstract class DeviceConnection {

    private final DeviceConnectionListener deviceConnectionListener;

    protected DeviceConnection(DeviceConnectionListener deviceConnectionListener) {
        this.deviceConnectionListener = deviceConnectionListener;
    }

    protected final DeviceConnectionListener deviceConnectionListener() {
        return deviceConnectionListener;
    }

    protected abstract void connect();

    protected abstract void disconnect();

    interface DeviceConnectionListener {
        void onDeviceConnected();

        void onDeviceDisconnected();
    }
}
