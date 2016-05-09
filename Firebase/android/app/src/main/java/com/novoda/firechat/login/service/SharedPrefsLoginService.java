package com.novoda.firechat.login.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.novoda.firechat.chat.login.LoginService;
import com.novoda.firechat.chat.login.model.User;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class SharedPrefsLoginService implements LoginService {

    private static final String KEY_USER = "KEY_USER";
    private final SharedPreferences sharedPreferences;

    private final BehaviorRelay<User> userRelay = BehaviorRelay.create();

    public SharedPrefsLoginService(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                String userName = sharedPreferences.getString(KEY_USER, null);
                if (userName != null) {
                    subscriber.onNext(new User(userName));
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void login(User user) {
        sharedPreferences.edit().putString(KEY_USER, user.getName()).apply();
        userRelay.call(user);
    }

}
