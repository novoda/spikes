package com.novoda.bonfire.user.service;

import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.user.data.model.Users;

import java.util.List;

import rx.Observable;

public interface UserService {

    Observable<Users> getAllUsers();

    Observable<ChannelWriteResult<Users>> getUsersForIds(List<String> userIds);
}
