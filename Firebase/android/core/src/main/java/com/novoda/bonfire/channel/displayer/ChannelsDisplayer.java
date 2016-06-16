package com.novoda.bonfire.channel.displayer;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;

public interface ChannelsDisplayer {

    void display(Channels channels);

    void attach(ChannelsInteractionListener channelsInteractionListener);

    void detach(ChannelsInteractionListener channelsInteractionListener);

    interface ChannelsInteractionListener {
        void onChannelSelected(Channel channel);

        void onAddNewChannel();

        void onInviteUsersClicked();
    }
}
