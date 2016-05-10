package com.novoda.firechat.login.service;

import com.novoda.firechat.login.data.model.Authentication;

import rx.Observable;

public interface LoginService {

    Observable<Authentication> getAuthentication();

    void login(String idToken);

}
