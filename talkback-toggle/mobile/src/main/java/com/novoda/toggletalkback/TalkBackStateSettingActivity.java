package com.novoda.toggletalkback;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class TalkBackStateSettingActivity extends AppCompatActivity {

    private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";
    private static final String VALUE_DISABLED = "0";
    private static final String VALUE_ENABLED = "1";

    public static final String EXTRA_ENABLE_TALKBACK = "enable_talkback";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENABLE_TALKBACK)) {
            updateTalkBackState(intent.getBooleanExtra(EXTRA_ENABLE_TALKBACK, false));
        }
        finish();
    }

    private void updateTalkBackState(boolean enableTalkBack) {
        if (enableTalkBack) {
            enableAccessibilityService(TALKBACK_SERVICE_NAME);
        } else {
            disableAccessibilityServices();
        }
    }

    private void enableAccessibilityService(String name) {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, name);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_ENABLED);
    }

    private void disableAccessibilityServices() {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "");
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_DISABLED);
    }
}
