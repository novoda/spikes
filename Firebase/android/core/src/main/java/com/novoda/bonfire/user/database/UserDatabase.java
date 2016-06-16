package com.novoda.bonfire.user.database;

import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

import rx.Observable;

public interface UserDatabase {

    Observable<Users> observeUsers();

    Observable<User> readUserFrom(String userId);

    void writeCurrentUser(User user);

    Observable<User> observeUser(String userId);

}
