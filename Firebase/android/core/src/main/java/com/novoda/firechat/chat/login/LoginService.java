package com.novoda.firechat.chat.login;

import com.novoda.firechat.chat.login.model.User;

import rx.Observable;

public interface LoginService {

    Observable<User> getUser();

    void login(User user);

}
