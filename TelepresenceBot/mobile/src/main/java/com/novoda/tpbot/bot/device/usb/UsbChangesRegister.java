package com.novoda.tpbot.bot.device.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

class UsbChangesRegister {

    static final String ACTION_USB_PERMISSION = "com.novoda.tpbot.USB_PERMISSION";

    private final Context context;
    private UsbChangesListener usbChangesListener;

    UsbChangesRegister(Context context) {
        this.context = context;
    }

    void register(UsbChangesListener usbChangesListener) {
        this.usbChangesListener = usbChangesListener;
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbChangesReceiever, filter);
    }

    void unregister() {
        context.unregisterReceiver(usbChangesReceiever);
    }

    private final BroadcastReceiver usbChangesReceiever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbChangesListenerIntentHandler.IntentHandler intentHandler = UsbChangesListenerIntentHandler.get(intent.getAction());
            intentHandler.handle(intent, usbChangesListener);
        }
    };

}
