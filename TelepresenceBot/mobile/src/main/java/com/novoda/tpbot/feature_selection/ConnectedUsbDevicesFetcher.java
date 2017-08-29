package com.novoda.tpbot.feature_selection;

import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.novoda.tpbot.R;

import java.util.HashMap;

class ConnectedUsbDevicesFetcher {

    private final UsbManager usbManager;
    private final Resources resources;

    ConnectedUsbDevicesFetcher(UsbManager usbManager, Resources resources) {
        this.usbManager = usbManager;
        this.resources = resources;
    }

    String fetchAsString() {
        HashMap<String, UsbDevice> devices = usbManager.getDeviceList();
        StringBuilder builder = new StringBuilder();
        if (devices.isEmpty()) {
            builder.append(resources.getString(R.string.no_connected_devices));
        } else {
            for (UsbDevice device : devices.values()) {
                builder.append(
                        resources.getString(
                                R.string.usb_device_name_vendor_product,
                                device.getDeviceName(),
                                device.getVendorId(),
                                device.getProductId()
                        )
                ).append("\n");
            }
        }
        return builder.toString();
    }

}
