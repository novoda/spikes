package com.novoda.bonfire.channel.displayer;

import com.novoda.bonfire.channel.data.model.Channel;

public interface CreateChannelDisplayer {

    void attach(InteractionListener interactionListener);

    void detach(InteractionListener interactionListener);

    void enableChannelCreation();

    void disableChannelCreation();

    void enableAddingMembers();

    void disableAddingMembers();

    interface InteractionListener {

        void onChannelNameLengthChanged(int length);

        void onPrivateChannelSwitchStateChanged(boolean isChecked);

        void onCreateChannel(Channel channel);

        InteractionListener NO_OP = new InteractionListener() {
            @Override
            public void onChannelNameLengthChanged(int length) {
                // empty implementation
            }

            @Override
            public void onPrivateChannelSwitchStateChanged(boolean isChecked) {
                // empty implementation
            }

            @Override
            public void onCreateChannel(Channel channel) {
                // empty implementation
            }
        };
    }
}
