package com.novoda.bonfire.channel.displayer;

public interface NewChannelDisplayer {

    void attach(ChannelCreationListener channelCreationListener);

    void detach(ChannelCreationListener channelCreationListener);

    void showChannelCreationError();

    interface ChannelCreationListener {

        void onCreateChannelClicked(String channelName, boolean isPrivate);

        void onCancel();
    }
}
