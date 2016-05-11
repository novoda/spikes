package com.novoda.bonfire.login.presenter;

import rx.Subscription;
import rx.functions.Action1;

public class LoginPresenter {

    private final com.novoda.bonfire.login.service.LoginService loginService;
    private final com.novoda.bonfire.login.displayer.LoginDisplayer loginDisplayer;
    private final com.novoda.bonfire.navigation.LoginNavigator navigator;

    private Subscription subscription;

    public LoginPresenter(com.novoda.bonfire.login.service.LoginService loginService, com.novoda.bonfire.login.displayer.LoginDisplayer loginDisplayer, com.novoda.bonfire.navigation.LoginNavigator navigator) {
        this.loginService = loginService;
        this.loginDisplayer = loginDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        navigator.attach(loginResultListener);
        loginDisplayer.attach(actionListener);
        subscription = loginService.getAuthentication()
                .subscribe(new Action1<com.novoda.bonfire.login.data.model.Authentication>() {
                    @Override
                    public void call(com.novoda.bonfire.login.data.model.Authentication authentication) {
                        if (authentication.isSuccess()) {
                            navigator.toChat();
                        } else {
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

    private final com.novoda.bonfire.login.displayer.LoginDisplayer.LoginActionListener actionListener = new com.novoda.bonfire.login.displayer.LoginDisplayer.LoginActionListener() {

        @Override
        public void onGooglePlusLoginSelected() {
            navigator.toGooglePlusLogin();
        }

    };

    private final com.novoda.bonfire.navigation.LoginNavigator.LoginResultListener loginResultListener = new com.novoda.bonfire.navigation.LoginNavigator.LoginResultListener() {
        @Override
        public void onGooglePlusLoginSuccess(String tokenId) {
            loginService.loginWithGoogle(tokenId);
        }

        @Override
        public void onGooglePlusLoginFailed(String statusMessage) {
            loginDisplayer.showAuthenticationError(statusMessage);
        }
    };


}
