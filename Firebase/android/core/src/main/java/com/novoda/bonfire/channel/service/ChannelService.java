package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.user.data.model.User;

import java.util.List;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannelsFor(User user);

    Observable<DatabaseResult<Channel>> createPublicChannel(Channel newChannel);

    Observable<DatabaseResult<Channel>> createPrivateChannel(Channel newChannel, User owner);

    Observable<DatabaseResult<List<String>>> addOwnerToPrivateChannel(Channel channel, User newOwner);

    Observable<DatabaseResult<List<String>>> removeOwnerFromPrivateChannel(Channel channel, User removedOwner);

    Observable<DatabaseResult<List<String>>> getOwnersOfChannel(Channel channel);
}
