package com.novoda.bonfire.login.service;

import com.novoda.bonfire.login.data.model.Authentication;

import rx.Observable;

public interface LoginService<T> {

    Observable<Authentication> getAuthentication();

    void loginWithGoogle(String idToken);

}
