package com.novoda.bonfire.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.link.FirebaseDynamicLinkFactory;
import com.novoda.bonfire.navigation.AndroidNavigator;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.bonfire.welcome.presenter.WelcomePresenter;

public class WelcomeActivity extends BaseActivity {

    private WelcomePresenter welcomePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String sender = getIntent().getData().getQueryParameter(FirebaseDynamicLinkFactory.SENDER);
        welcomePresenter = new WelcomePresenter(
                Dependencies.INSTANCE.getUserService(),
                (WelcomeDisplayer) findViewById(R.id.welcome_view),
                new AndroidNavigator(this),
                Dependencies.INSTANCE.getAnalytics(),
                sender
        );
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
