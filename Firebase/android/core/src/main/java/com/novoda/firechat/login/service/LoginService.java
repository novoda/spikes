package com.novoda.firechat.login.service;

import com.novoda.firechat.login.data.model.Authentication;

import rx.Observable;

public interface LoginService<T> {

    Observable<Authentication> getAuthentication();

    void loginWithGoogle(String idToken);

}
