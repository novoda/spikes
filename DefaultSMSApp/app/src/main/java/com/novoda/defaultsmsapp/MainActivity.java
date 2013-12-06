package com.novoda.defaultsmsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;

public class MainActivity extends Activity {

    private static final String DEVICE_DEFAULT_SMS_PACKAGE_KEY = "com.novoda.defaultsmsapp.deviceDefaultSmsPackage";
    private static final String INVALID_PACKAGE = "invalid_package";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        saveDeviceDefaultSmsPackage();
    }

    private void saveDeviceDefaultSmsPackage() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (hasNoPreviousSmsDefaultPackage(preferences)) {
            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this);
            preferences.edit().putString(DEVICE_DEFAULT_SMS_PACKAGE_KEY, defaultSmsPackage).apply();
        }
    }

    private boolean hasNoPreviousSmsDefaultPackage(SharedPreferences preferences) {
        return !preferences.contains(DEVICE_DEFAULT_SMS_PACKAGE_KEY);
    }

    private void setUpViews() {
        findViewById(R.id.set_as_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceDefaultSmsPackage(getPackageName());
            }
        });

        findViewById(R.id.restore_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceDefaultSmsPackage(getPreviousSmsDefaultPackage());
            }
        });
    }

    private void setDeviceDefaultSmsPackage(String packageName) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
        startActivity(intent);
    }

    private String getPreviousSmsDefaultPackage() {
        return getPreferences(MODE_PRIVATE).getString(DEVICE_DEFAULT_SMS_PACKAGE_KEY, INVALID_PACKAGE);
    }
}
