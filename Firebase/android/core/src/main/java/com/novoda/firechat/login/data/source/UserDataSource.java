package com.novoda.firechat.login.data.source;

import com.novoda.firechat.login.data.model.User;

import rx.Observable;

public interface UserDataSource {

    Observable<User> readUser();

    void saveUser(User user);

}
