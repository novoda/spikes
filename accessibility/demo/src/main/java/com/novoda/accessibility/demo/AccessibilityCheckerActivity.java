package com.novoda.accessibility.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityChecker;

public class AccessibilityCheckerActivity extends AppCompatActivity {

    private AccessibilityChecker accessibilityChecker;
    private TextView talkbackStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_checker);

        accessibilityChecker = AccessibilityChecker.newInstance(this);
        talkbackStatus = ((TextView) findViewById(R.id.talkback_status));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accessibilityChecker.isSpokenFeedbackEnabled()) {
            talkbackStatus.setText("TalkBack is enabled");
        } else {
            talkbackStatus.setText("TalkBack is not enabled");
        }
    }

}
