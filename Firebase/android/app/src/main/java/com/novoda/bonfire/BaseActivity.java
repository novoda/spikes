package com.novoda.bonfire;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected BonfireApplication getFireChatApplication() {
        return (BonfireApplication) getApplication();
    }

}
