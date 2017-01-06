package com.novoda.toggletalkback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        }, 1500);
    }

    public void enableTalkBack(View view) {
        setTalkBackEnabled(true);
    }

    public void disableTalkBack(View view) {
        setTalkBackEnabled(false);
    }

    private void setTalkBackEnabled(boolean value) {
        Intent intent = new Intent(this, TalkBackStateSettingActivity.class);
        intent.putExtra(TalkBackStateSettingActivity.EXTRA_ENABLE_TALKBACK, value);
        startActivity(intent);
    }

}
