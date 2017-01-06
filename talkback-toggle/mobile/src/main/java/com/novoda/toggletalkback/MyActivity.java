package com.novoda.toggletalkback;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyActivity extends AppCompatActivity {

    private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";
    private static final String VALUE_DISABLED = "0";
    private static final String VALUE_ENABLED = "1";

    private AccessibilityServices accessibilityServices;

    @BindView(R.id.talkback_status)
    TextView talkbackStatusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);

        accessibilityServices = AccessibilityServices.newInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTalkBackStatusTextView();
    }

    private void updateTalkBackStatusTextView() {
        talkbackStatusTextView.setText("checking status");
        talkbackStatusTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityServices.isSpokenFeedbackEnabled()) {
                    talkbackStatusTextView.setText("talkback enabled");
                } else {
                    talkbackStatusTextView.setText("talkback disabled");
                }
            }
        }, 3000);
    }

    public void enableTalkBack(View view) {
        enableAccessibilityService(TALKBACK_SERVICE_NAME);
        updateTalkBackStatusTextView();
    }

    public void disableTalkBack(View view) {
        disableAccessibilityServices();
        updateTalkBackStatusTextView();
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
