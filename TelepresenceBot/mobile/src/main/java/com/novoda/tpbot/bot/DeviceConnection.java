package com.novoda.tpbot.bot;

abstract class DeviceConnection {

    private final DeviceConnectionListener deviceConnectionListener;

    DeviceConnection(DeviceConnectionListener deviceConnectionListener) {
        this.deviceConnectionListener = deviceConnectionListener;
    }

    final DeviceConnectionListener deviceConnectionListener() {
        return deviceConnectionListener;
    }

    protected abstract void connect();

    protected abstract void disconnect();

    protected abstract void send(String data);

    interface DeviceConnectionListener {
        void onDeviceConnected();

        void onDeviceDisconnected();

        void onDataReceived(String data);

    }
}
