package com.novoda.firechat.login.service;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.novoda.firechat.login.data.model.User;
import com.novoda.firechat.login.data.source.UserDataSource;

import rx.Observable;
import rx.functions.Func0;

public class LocalLoginService implements LoginService {

    private static final String KEY_USER = "KEY_USER";

    private final UserDataSource userDataSource;

    private final BehaviorRelay<User> userRelay = BehaviorRelay.create();

    public LocalLoginService(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Override
    public Observable<User> getUser() {
        return userRelay
                .startWith(initRelay());
    }

    private Observable<User> initRelay() {
        return Observable.defer(new Func0<Observable<User>>() {
            @Override
            public Observable<User> call() {
                if (userRelay.hasValue()) {
                    return Observable.empty();
                } else {
                    return fetchUser();
                }
            }
        });
    }

    private Observable<User> fetchUser() {
        return userDataSource.readUser();
    }

    @Override
    public void login(User user) {
        userDataSource.saveUser(user);
        userRelay.call(user);
    }

}
