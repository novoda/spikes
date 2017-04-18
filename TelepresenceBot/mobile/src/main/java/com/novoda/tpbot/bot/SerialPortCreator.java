package com.novoda.tpbot.bot;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.felhr.usbserial.UsbSerialDevice;

class SerialPortCreator {

    UsbSerialDevice create(UsbDevice usbDevice, UsbDeviceConnection connection) {
        return UsbSerialDevice.createUsbSerialDevice(usbDevice, connection);
    }

}
