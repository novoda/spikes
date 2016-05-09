package com.novoda.firechat.login;

import android.os.Bundle;

import com.novoda.firechat.BaseActivity;
import com.novoda.firechat.R;
import com.novoda.firechat.login.displayer.LoginDisplayer;
import com.novoda.firechat.login.presenter.LoginPresenter;
import com.novoda.firechat.navigation.AndroidNavigator;

public class LoginActivity extends BaseActivity {

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginDisplayer loginDisplayer = (LoginDisplayer) findViewById(R.id.loginView);
        presenter = new LoginPresenter(getFireChatApplication().getLoginService(), loginDisplayer, new AndroidNavigator(this));
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
