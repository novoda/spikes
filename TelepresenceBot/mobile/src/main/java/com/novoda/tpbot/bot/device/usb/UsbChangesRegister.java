package com.novoda.tpbot.bot.device.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import androidx.annotation.Nullable;

class UsbChangesRegister {

    static final String ACTION_USB_PERMISSION = "com.novoda.tpbot.USB_PERMISSION";

    private final Context context;

    @Nullable
    private UsbChangesBroadcastReceiver usbChangesBroadcastReceiver;

    UsbChangesRegister(Context context) {
        this.context = context;
    }

    void register(UsbChangesListener usbChangesListener) {
        if (usbChangesBroadcastReceiver == null) {
            usbChangesBroadcastReceiver = new UsbChangesBroadcastReceiver(usbChangesListener);
        }

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbChangesBroadcastReceiver, filter);
    }

    void unregister() {
        if (usbChangesBroadcastReceiver != null) {
            context.unregisterReceiver(usbChangesBroadcastReceiver);
            usbChangesBroadcastReceiver = null;
        }

    }

    private class UsbChangesBroadcastReceiver extends BroadcastReceiver {

        private final UsbChangesListener usbChangesListener;

        private UsbChangesBroadcastReceiver(UsbChangesListener usbChangesListener) {
            this.usbChangesListener = usbChangesListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            UsbChangesListenerIntentHandler.IntentHandler intentHandler = UsbChangesListenerIntentHandler.get(intent.getAction());
            intentHandler.handle(intent, usbChangesListener);
        }
    }

}
