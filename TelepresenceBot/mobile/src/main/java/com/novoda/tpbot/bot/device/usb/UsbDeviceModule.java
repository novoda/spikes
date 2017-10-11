package com.novoda.tpbot.bot.device.usb;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;

import com.novoda.tpbot.bot.device.ConnectedDevicesFetcher;
import com.novoda.tpbot.bot.device.DeviceConnection;

import dagger.Module;
import dagger.Provides;

@Module
public class UsbDeviceModule {

    @Provides
    DeviceConnection provideUsbDeviceConnection(Context context, DeviceConnection.DeviceConnectionListener deviceConnectionListener) {
        UsbChangesRegister usbChangesRegister = new UsbChangesRegister(context);
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        SupportedDeviceRetriever supportedDeviceRetriever = new SupportedDeviceRetriever(usbManager);
        SerialPortCreator serialPortCreator = new SerialPortCreator();
        SerialPortMonitor serialPortMonitor = new SerialPortMonitor(usbManager, serialPortCreator, );
        UsbPermissionRequester usbPermissionRequester = new UsbPermissionRequester(context, usbManager);

        return new UsbDeviceConnection(
                deviceConnectionListener,
                usbChangesRegister,
                supportedDeviceRetriever,
                serialPortMonitor,
                usbPermissionRequester
        );
    }

    @Provides
    ConnectedDevicesFetcher provideConnectedDevicesFetcher(UsbManager usbManager, Resources resources) {
        return new ConnectedUsbDevicesFetcher(usbManager, resources);
    }

}
