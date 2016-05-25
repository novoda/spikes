package com.novoda.bonfire.channel.database;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.user.data.model.User;

import java.util.List;

import rx.Observable;

public interface ChannelsDatabase {

    Observable<List<String>> getPublicChannelIds();

    Observable<List<String>> getPrivateChannelIdsFor(User user);

    Observable<Channel> getChannelFor(String channelName);

    Observable<Channel> writeChannel(Channel newChannel);

    Observable<Channel> writeChannelToPublicChannelIndex(Channel newChannel);

    Observable<Channel> addOwnerToPrivateChannel(User user, Channel channel);

    Observable<Channel> removeOwnerFromPrivateChannel(User user, Channel channel);

    Observable<Channel> addChannelToUserPrivateChannelIndex(User user, Channel channel);

    Observable<Channel> removeChannelFromUserPrivateChannelIndex(User user, Channel channel);

    Observable<List<String>> getOwnerIdsFor(Channel channel);

    Observable<User> getUserFrom(String userId);

}
