package com.novoda.firechat.login;

import android.content.Intent;
import android.os.Bundle;

import com.novoda.firechat.BaseActivity;
import com.novoda.firechat.R;
import com.novoda.firechat.login.displayer.LoginDisplayer;
import com.novoda.firechat.login.presenter.LoginPresenter;
import com.novoda.firechat.navigation.AndroidLoginNavigator;
import com.novoda.firechat.navigation.AndroidNavigator;

public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 42;

    private LoginPresenter presenter;
    private AndroidLoginNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginDisplayer loginDisplayer = (LoginDisplayer) findViewById(R.id.loginView);
        navigator = new AndroidLoginNavigator(this, new AndroidNavigator(this));
        presenter = new LoginPresenter(getFireChatApplication().getLoginService(), loginDisplayer, navigator);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!navigator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
