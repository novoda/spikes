package com.novoda.bonfire.user.service;

import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.database.UserDatabase;

import rx.Observable;

public class PersistedUserService implements UserService {

    private final UserDatabase userDatabase;

    public PersistedUserService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public Observable<Users> getAllUsers() {
        return userDatabase.observeUsers();
    }

}
