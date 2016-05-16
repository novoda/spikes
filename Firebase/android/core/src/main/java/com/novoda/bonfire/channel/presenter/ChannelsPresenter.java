package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.navigation.Navigator;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ChannelsPresenter {

    private final ChannelsDisplayer channelsDisplayer;
    private final ChannelService channelService;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public ChannelsPresenter(ChannelsDisplayer channelsDisplayer, ChannelService channelService, Navigator navigator) {
        this.channelsDisplayer = channelsDisplayer;
        this.channelService = channelService;
        this.navigator = navigator;
    }

    public void startPresenting() {
        channelsDisplayer.attach(channelSelectionListener);
        subscriptions.add(channelService.getChannels().subscribe(new Action1<Channels>() {
            @Override
            public void call(Channels channels) {
                channelsDisplayer.display(channels);
            }
        }));
    }

    public void stopPresenting() {
        subscriptions.clear();
        channelsDisplayer.detach(channelSelectionListener);
        subscriptions = new CompositeSubscription();
    }

    private final ChannelsDisplayer.ChannelSelectionListener channelSelectionListener = new ChannelsDisplayer.ChannelSelectionListener() {
        @Override
        public void onChannelSelected(Channel channel) {
            navigator.toChannel(channel);
        }
    };
}
