package com.novoda.defaultsmsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d("SmsKitKat", "SmsReceiver onReceive – " + intent.toString());
    }
}
