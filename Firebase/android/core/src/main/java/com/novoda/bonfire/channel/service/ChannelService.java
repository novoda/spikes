package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.user.data.model.User;

import java.util.List;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannelsFor(User user);

    Observable<ChannelWriteResult<Channel>> createPublicChannel(Channel newChannel);

    Observable<ChannelWriteResult<Channel>> createPrivateChannel(Channel newChannel, User owner);

    Observable<ChannelWriteResult<List<String>>> addOwnerToPrivateChannel(Channel channel, User newOwner);

    Observable<ChannelWriteResult<List<String>>> removeOwnerFromPrivateChannel(Channel channel, User removedOwner);

    Observable<ChannelWriteResult<List<String>>> getOwnersOfChannel(Channel channel);
}
