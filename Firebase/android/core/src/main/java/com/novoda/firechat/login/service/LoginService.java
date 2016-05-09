package com.novoda.firechat.login.service;

import com.novoda.firechat.login.data.model.User;

import rx.Observable;

public interface LoginService {

    Observable<User> getUser();

    void login(User user);

}
