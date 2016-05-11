package com.novoda.bonfire.login.service;

import rx.Observable;

public interface LoginService<T> {

    Observable<com.novoda.bonfire.login.data.model.Authentication> getAuthentication();

    void loginWithGoogle(String idToken);

}
