package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.login.data.model.User;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannelsFor(User user);

    Observable<ChannelWriteResult> createPublicChannel(Channel newChannel);
}
