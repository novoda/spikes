package com.novoda.firechat.chat.login.presenter;

import com.novoda.firechat.chat.login.LoginService;
import com.novoda.firechat.chat.login.displayer.LoginDisplayer;
import com.novoda.firechat.chat.login.model.User;
import com.novoda.firechat.chat.navigation.Navigator;

import rx.Subscription;
import rx.functions.Action1;

public class LoginPresenter {

    private final LoginService loginService;
    private final LoginDisplayer loginDisplayer;
    private final Navigator navigator;

    private Subscription subscription;

    public LoginPresenter(LoginService loginService, LoginDisplayer loginDisplayer, Navigator navigator) {
        this.loginService = loginService;
        this.loginDisplayer = loginDisplayer;
        this.navigator = navigator;
    }

    public void startPresenting() {
        loginDisplayer.attach(actionListener);
        subscription = loginService.getUser()
                .subscribe(new Action1<User>() { //TODO handle error flow
                    @Override
                    public void call(User user) {
                        navigator.toChat();
                    }
                });
    }

    public void stopPresenting() {
        loginDisplayer.detach(actionListener);
        subscription.unsubscribe(); //TODO handle checks
    }

    private final LoginDisplayer.LoginActionListener actionListener = new LoginDisplayer.LoginActionListener() {
        @Override
        public void onUserNameEntered(String userName) {
            loginService.login(new User(userName));
        }
    };

}
