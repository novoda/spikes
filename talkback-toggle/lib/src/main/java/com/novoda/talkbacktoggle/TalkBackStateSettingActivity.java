package com.novoda.talkbacktoggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class TalkBackStateSettingActivity extends Activity {

    static final String ACTION_ENABLE_TALKBACK = "com.novoda.talkbacktoggle.ENABLE_TALKBACK";
    static final String ACTION_DISABLE_TALKBACK = "com.novoda.talkbacktoggle.DISABLE_TALKBACK";

    private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";
    private static final String VALUE_DISABLED = "0";
    private static final String VALUE_ENABLED = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (intent == null) {
            finish();
            return;
        }

        if (ACTION_ENABLE_TALKBACK.equals(intent.getAction())) {
            updateTalkBackState(true);
        } else if (ACTION_DISABLE_TALKBACK.equals(intent.getAction())) {
            updateTalkBackState(false);
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
