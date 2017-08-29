package com.novoda.tpbot.bot.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.novoda.support.Optional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class SupportedDeviceRetriever {

    private static final List<Integer> SUPPORTED_VENDOR_IDS = Arrays.asList(9025, 10755, 4292); // TODO: read from devices_filter.xml

    private final UsbManager usbManager;

    SupportedDeviceRetriever(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    Optional<UsbDevice> retrieveFirstSupportedUsbDevice() {
        Map<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        for (UsbDevice usbDevice : usbDevices.values()) {
            int deviceVID = usbDevice.getVendorId();

            if (isSupportedDeviceID(deviceVID)) {
                return Optional.of(usbDevice);
            }
        }

        return Optional.absent();
    }

    private boolean isSupportedDeviceID(int deviceVID) {
        return SUPPORTED_VENDOR_IDS.contains(deviceVID);
    }

}
