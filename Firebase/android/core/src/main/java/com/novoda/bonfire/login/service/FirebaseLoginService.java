package com.novoda.bonfire.login.service;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.database.AuthDatabase;
import com.novoda.bonfire.user.database.UserDatabase;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;

public class FirebaseLoginService implements LoginService {

    private final AuthDatabase authDatabase;
    private final UserDatabase userDatabase;
    private final BehaviorRelay<Authentication> authRelay;

    public FirebaseLoginService(AuthDatabase authDatabase, UserDatabase userDatabase) {
        this.authDatabase = authDatabase;
        this.userDatabase = userDatabase;
        authRelay = BehaviorRelay.create();
    }

    @Override
    public Observable<Authentication> getAuthentication() {
        return authRelay
                .startWith(initRelay());
    }

    private Observable<Authentication> initRelay() {
        return Observable.defer(new Func0<Observable<Authentication>>() {
            @Override
            public Observable<Authentication> call() {
                if (authRelay.hasValue() && authRelay.getValue().isSuccess()) {
                    return Observable.empty();
                } else {
                    return fetchUser();
                }
            }
        });
    }

    private Observable<Authentication> fetchUser() {
        return authDatabase.readAuthentication()
                .doOnNext(authRelay)
                .ignoreElements();
    }

    @Override
    public void loginWithGoogle(String idToken) {
        authDatabase.loginWithGoogle(idToken)
                .subscribe(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
                        if (authentication.isSuccess()) {
                            userDatabase.writeCurrentUser(authentication.getUser());
                        }
                        authRelay.call(authentication);
                    }
                });
    }

}
