package com.novoda.toggletalkback;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class TalkBackStateSettingActivity extends AppCompatActivity {

    static final String ACTION_ENABLE_TALKBACK = "com.novoda.toggletalkback.ENABLE_TALKBACK";
    static final String ACTION_DISABLE_TALKBACK = "com.novoda.toggletalkback.DISABLE_TALKBACK";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (intent == null) {
            finish();
            return;
        }

        Toggler toggler = createTalkBackToggler();

        if (ACTION_ENABLE_TALKBACK.equals(intent.getAction())) {
            toggler.enableTalkBack();
        } else if (ACTION_DISABLE_TALKBACK.equals(intent.getAction())) {
            toggler.disableTalkBack();
        }

        finish();
    }

    private Toggler createTalkBackToggler() {
        ContentResolver contentResolver = getContentResolver();
        Toggler.Callback togglerCallback = createTogglerCallback();

        return new Toggler(contentResolver, togglerCallback);
    }

    private Toggler.Callback createTogglerCallback() {
        return new Toggler.Callback() {
            @Override
            public void onSecurityExceptionThrown() {
                String message = "Need to grant permission. Check README.md";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Log.w("!!!", message);
            }
        };
    }

}
