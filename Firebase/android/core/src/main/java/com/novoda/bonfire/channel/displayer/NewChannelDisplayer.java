package com.novoda.bonfire.channel.displayer;

public interface NewChannelDisplayer {

    void attach(InteractionListener interactionListener);

    void detach(InteractionListener interactionListener);

    void enableChannelCreation();

    void disableChannelCreation();

    void showChannelCreationError();

    interface InteractionListener {

        void onChannelNameLengthChanged(int length);

        void onCreateChannelClicked(String channelName, boolean isPrivate);

        InteractionListener NO_OP = new InteractionListener() {
            @Override
            public void onChannelNameLengthChanged(int length) {
                // empty implementation
            }

            @Override
            public void onCreateChannelClicked(String channelName, boolean isPrivate) {
                // empty implementation
            }
        };
    }
}
