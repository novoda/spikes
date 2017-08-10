package com.novoda.tpbot.bot;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.novoda.notils.logger.simple.Log;
import com.novoda.support.Optional;

class UsbDeviceConnection extends DeviceConnection implements UsbChangesListener {

    private final UsbChangesRegister usbChangesRegister;
    private final SupportedDeviceRetriever supportedDeviceRetriever;
    private final SerialPortMonitor serialPortMonitor;
    private final UsbPermissionRequester usbPermissionRequester;

    private Optional<UsbDevice> supportedUsbDevice = Optional.absent();

    static UsbDeviceConnection newInstance(Context context, DeviceConnectionListener deviceConnectionListener) {
        UsbChangesRegister usbChangesRegister = new UsbChangesRegister(context);
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        SupportedDeviceRetriever supportedDeviceRetriever = new SupportedDeviceRetriever(usbManager);
        SerialPortMonitor.DataReceiver dataReceiver = new SerialPortMonitor.DataReceiver() {
            @Override
            public void onReceive(String data) {

            }
        };
        SerialPortCreator serialPortCreator = new SerialPortCreator();
        SerialPortMonitor serialPortMonitor = new SerialPortMonitor(usbManager, dataReceiver, serialPortCreator);
        UsbPermissionRequester usbPermissionRequester = new UsbPermissionRequester(context, usbManager);

        return new UsbDeviceConnection(
                deviceConnectionListener,
                usbChangesRegister,
                supportedDeviceRetriever,
                serialPortMonitor,
                usbPermissionRequester
        );
    }

    protected UsbDeviceConnection(DeviceConnectionListener deviceConnectionListener,
                                  UsbChangesRegister usbChangesRegister,
                                  SupportedDeviceRetriever supportedDeviceRetriever,
                                  SerialPortMonitor serialPortMonitor,
                                  UsbPermissionRequester usbPermissionRequester) {
        super(deviceConnectionListener);
        this.usbChangesRegister = usbChangesRegister;
        this.supportedDeviceRetriever = supportedDeviceRetriever;
        this.serialPortMonitor = serialPortMonitor;
        this.usbPermissionRequester = usbPermissionRequester;
    }

    @Override
    protected void connect() {
        usbChangesRegister.register(this);
    }

    @Override
    public void onDeviceAttached() {
        openConnection();
    }

    private void openConnection() {
        if (supportedUsbDevice.isPresent()) {
            return;
        }

        supportedUsbDevice = supportedDeviceRetriever.retrieveFirstSupportedUsbDevice();

        if (supportedUsbDevice.isPresent()) {
            usbPermissionRequester.requestPermission(supportedUsbDevice.get());
        } else {
            Log.d(getClass().getSimpleName(), "startConnection: Could not find a supported USB device.");
        }
    }

    @Override
    public void onPermissionGranted() {
        boolean canMonitor = serialPortMonitor.tryToMonitorSerialPortFor(supportedUsbDevice.get());

        if (canMonitor) {
            deviceConnectionListener().onDeviceConnected();
        } else {
            closeConnection();
        }
    }

    @Override
    protected void disconnect() {
        usbChangesRegister.unregister();
        closeConnection();
    }

    private void closeConnection() {
        serialPortMonitor.stopMonitoring();
        supportedUsbDevice = Optional.absent();
        deviceConnectionListener().onDeviceDisconnected();
    }

    @Override
    public void onPermissionDenied() {
        closeConnection();
    }

    @Override
    public void onDeviceDetached() {
        closeConnection();
    }

}
