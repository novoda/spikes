package com.novoda.defaultsmsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MmsReceiver extends BroadcastReceiver {
    public MmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d("SmsKitKat", "MmsReceiver onReceive – " + intent.toString());
    }
}
