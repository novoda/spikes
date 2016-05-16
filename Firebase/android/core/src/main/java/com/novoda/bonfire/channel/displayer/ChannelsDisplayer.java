package com.novoda.bonfire.channel.displayer;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;

public interface ChannelsDisplayer {

    void display(Channels channels);

    void attach(ChannelSelectionListener channelSelectionListener);

    void detach(ChannelSelectionListener channelSelectionListener);

    interface ChannelSelectionListener {
        void onChannelSelected(Channel channel);

        ChannelSelectionListener NO_OP = new ChannelSelectionListener() {
            @Override
            public void onChannelSelected(Channel channel) {
                // this does not do anything
            }
        };
    }
}
