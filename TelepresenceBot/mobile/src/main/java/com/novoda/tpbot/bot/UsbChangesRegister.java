package com.novoda.tpbot.bot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import static com.novoda.tpbot.bot.AndroidMovementService.ACTION_USB_PERMISSION;

class UsbChangesRegister {

    private final Context context;
    private UsbChangesListener usbChangesListener;

    private UsbChangesRegister(Context context) {
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
