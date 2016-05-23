package com.novoda.bonfire.user.service;

import com.novoda.bonfire.user.data.model.Users;

import rx.Observable;

public interface UserService {

    Observable<Users> getAllUsers();
}
