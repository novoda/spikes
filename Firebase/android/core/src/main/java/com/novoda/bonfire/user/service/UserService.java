package com.novoda.bonfire.user.service;

import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.Users;

import java.util.List;

import rx.Observable;

public interface UserService {

    Observable<Users> getAllUsers();

    Observable<DatabaseResult<Users>> getUsersForIds(List<String> userIds);
}
