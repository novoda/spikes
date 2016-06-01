package com.novoda.bonfire.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.R;
import com.novoda.bonfire.navigation.AndroidNavigator;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.bonfire.welcome.presenter.WelcomePresenter;
import com.novoda.notils.logger.simple.Log;

public class WelcomeActivity extends BaseActivity {

    private WelcomePresenter welcomePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Got it!");
        setContentView(R.layout.activity_welcome);
        String sender = getIntent().getData().getQueryParameter("sender");
        welcomePresenter = new WelcomePresenter((WelcomeDisplayer) findViewById(R.id.welcomeView),
                                                new AndroidNavigator(this),
                                                sender);
    }

    @Override
    protected void onStart() {
        super.onStart();
        welcomePresenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        welcomePresenter.stopPresenting();
    }
}
