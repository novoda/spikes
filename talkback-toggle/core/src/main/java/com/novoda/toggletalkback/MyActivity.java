package com.novoda.toggletalkback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.novoda.toggletalkback.TalkBackStateSettingActivity.ACTION_DISABLE_TALKBACK;
import static com.novoda.toggletalkback.TalkBackStateSettingActivity.ACTION_ENABLE_TALKBACK;

public class MyActivity extends AppCompatActivity {

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
        talkbackStatusTextView.setText("Checking TalkBack status");

        talkbackStatusTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityServices.isSpokenFeedbackEnabled()) {
                    talkbackStatusTextView.setText("TalkBack enabled");
                } else {
                    talkbackStatusTextView.setText("TalkBack disabled");
                }
            }
        }, 1500);
    }

    public void enableTalkBack(View view) {
        startActivity(new Intent(ACTION_ENABLE_TALKBACK));
    }

    public void disableTalkBack(View view) {
        startActivity(new Intent(ACTION_DISABLE_TALKBACK));
    }

}
