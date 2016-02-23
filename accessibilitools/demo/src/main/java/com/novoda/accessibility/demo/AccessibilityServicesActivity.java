package com.novoda.accessibility.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;

public class AccessibilityServicesActivity extends AppCompatActivity {

    private AccessibilityServices accessibilityServices;
    private TextView talkbackStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_checker);

        accessibilityServices = AccessibilityServices.newInstance(this);
        talkbackStatus = ((TextView) findViewById(R.id.talkback_status));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accessibilityServices.isSpokenFeedbackEnabled()) {
            talkbackStatus.setText("Spoken feedback is enabled");
        } else {
            talkbackStatus.setText("Spoken feedback is not enabled");
        }
    }

}
