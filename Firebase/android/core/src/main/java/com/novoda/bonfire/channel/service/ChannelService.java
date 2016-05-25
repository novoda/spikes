package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannelsFor(User user);

    Observable<DatabaseResult<Channel>> createPublicChannel(Channel newChannel);

    Observable<DatabaseResult<Channel>> createPrivateChannel(Channel newChannel, User owner);

    Observable<DatabaseResult<User>> addOwnerToPrivateChannel(Channel channel, User newOwner);

    Observable<DatabaseResult<User>> removeOwnerFromPrivateChannel(Channel channel, User removedOwner);

    Observable<DatabaseResult<Users>> getOwnersOfChannel(Channel channel);
}
