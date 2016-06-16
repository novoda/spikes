package com.novoda.bonfire.login.presenter;

import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.analytics.ErrorLogger;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.displayer.LoginDisplayer;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.LoginNavigator;

import rx.Subscription;
import rx.functions.Action1;

public class LoginPresenter {

    private final LoginService loginService;
    private final LoginDisplayer loginDisplayer;
    private final LoginNavigator navigator;
    private final ErrorLogger errorLogger;
    private final Analytics analytics;

    private Subscription subscription;

    public LoginPresenter(LoginService loginService,
                          LoginDisplayer loginDisplayer,
                          LoginNavigator navigator,
                          ErrorLogger errorLogger,
                          Analytics analytics) {
        this.loginService = loginService;
        this.loginDisplayer = loginDisplayer;
        this.navigator = navigator;
        this.errorLogger = errorLogger;
        this.analytics = analytics;
    }

    public void startPresenting() {
        navigator.attach(loginResultListener);
        loginDisplayer.attach(actionListener);
        subscription = loginService.getAuthentication()
                .subscribe(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
                        if (authentication.isSuccess()) {
                            navigator.toChannels();
                        } else {
                            errorLogger.reportError(authentication.getFailure(), "Authentication failed");
                            loginDisplayer.showAuthenticationError(authentication.getFailure().getLocalizedMessage()); //TODO improve error display
                        }
                    }
                });
    }

    public void stopPresenting() {
        navigator.detach(loginResultListener);
        loginDisplayer.detach(actionListener);
        subscription.unsubscribe(); //TODO handle checks
    }

    private final LoginDisplayer.LoginActionListener actionListener = new LoginDisplayer.LoginActionListener() {

        @Override
        public void onGooglePlusLoginSelected() {
            analytics.trackSignInStarted("google");
            navigator.toGooglePlusLogin();
        }

    };

    private final LoginNavigator.LoginResultListener loginResultListener = new LoginNavigator.LoginResultListener() {
        @Override
        public void onGooglePlusLoginSuccess(String tokenId) {
            analytics.trackSignInSuccessful("google");
            loginService.loginWithGoogle(tokenId);
        }

        @Override
        public void onGooglePlusLoginFailed(String statusMessage) {
            loginDisplayer.showAuthenticationError(statusMessage);
        }
    };


}
