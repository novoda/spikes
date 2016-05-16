package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channels;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannels();
}
