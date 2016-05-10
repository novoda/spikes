package com.novoda.firechat.login.presenter;

import com.novoda.firechat.login.data.model.Authentication;
import com.novoda.firechat.login.displayer.LoginDisplayer;
import com.novoda.firechat.login.service.LoginService;
import com.novoda.firechat.navigation.LoginNavigator;

import rx.Subscription;
import rx.functions.Action1;

public class LoginPresenter {

    private final LoginService loginService;
    private final LoginDisplayer loginDisplayer;
    private final LoginNavigator navigator;

    private Subscription subscription;

    public LoginPresenter(LoginService loginService, LoginDisplayer loginDisplayer, LoginNavigator navigator) {
        this.loginService = loginService;
        this.loginDisplayer = loginDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        navigator.attach(loginResultListener);
        loginDisplayer.attach(actionListener);
        subscription = loginService.getAuthentication()
                .subscribe(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
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

    private final LoginDisplayer.LoginActionListener actionListener = new LoginDisplayer.LoginActionListener() {

        @Override
        public void onGooglePlusLoginSelected() {
            navigator.toGooglePlusLogin();
        }

    };

    private final LoginNavigator.LoginResultListener loginResultListener = new LoginNavigator.LoginResultListener() {
        @Override
        public void onGooglePlusLoginSuccess(String tokenId) {
            loginService.login(tokenId);
        }

        @Override
        public void onGooglePlusLoginFailed(String statusMessage) {
            loginDisplayer.showAuthenticationError(statusMessage);
        }
    };


}
