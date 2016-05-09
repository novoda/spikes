package com.novoda.firechat.login.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.novoda.firechat.login.data.model.User;

import rx.Observable;
import rx.Subscriber;

public class SharedPreferencesUserDataSource implements UserDataSource {

    private static final String KEY_USER = "KEY_USER";

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesUserDataSource(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public Observable<User> readUser() {
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
    public void saveUser(User user) {
        sharedPreferences.edit().putString(KEY_USER, user.getName()).apply();
    }

}
