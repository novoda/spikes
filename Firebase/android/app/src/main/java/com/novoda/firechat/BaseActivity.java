package com.novoda.firechat;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected FireChatApplication getFireChatApplication() {
        return (FireChatApplication) getApplication();
    }

}
