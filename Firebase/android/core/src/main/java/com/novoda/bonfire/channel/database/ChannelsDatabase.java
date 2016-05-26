package com.novoda.bonfire.channel.database;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.user.data.model.User;

import java.util.List;

import rx.Observable;

public interface ChannelsDatabase {

    Observable<List<String>> observePublicChannelIds();

    Observable<List<String>> observePrivateChannelIdsFor(User user);

    Observable<Channel> readChannelFor(String channelName);

    Observable<Channel> writeChannel(Channel newChannel);

    Observable<Channel> writeChannelToPublicChannelIndex(Channel newChannel);

    Observable<Channel> addOwnerToPrivateChannel(User user, Channel channel);

    Observable<Channel> removeOwnerFromPrivateChannel(User user, Channel channel);

    Observable<Channel> addChannelToUserPrivateChannelIndex(User user, Channel channel);

    Observable<Channel> removeChannelFromUserPrivateChannelIndex(User user, Channel channel);

    Observable<List<String>> observeOwnerIdsFor(Channel channel);

}
