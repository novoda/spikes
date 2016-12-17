package com.novoda.tpbot.bot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.novoda.notils.logger.toast.Toaster;

public class UsbReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toaster.newInstance(context).popToast("onReceive: " + intent.getAction());
    }
}
