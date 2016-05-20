package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.UserSearchResult;
import com.novoda.bonfire.channel.data.model.Users;

import rx.Observable;

public interface UserService {

    Observable<Users> getAllUsers();

    Observable<UserSearchResult> getUserWithName(String name);
}
