package com.novoda.bonfire;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected BonfireApplication getFireChatApplication() {
        return (BonfireApplication) getApplication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFireChatApplication().getAnalytics().trackActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getFireChatApplication().getAnalytics().trackActivityStop(this);
    }
}
