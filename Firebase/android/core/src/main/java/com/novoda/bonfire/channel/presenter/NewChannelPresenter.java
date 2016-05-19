package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelInfo;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.navigation.Navigator;

public class NewChannelPresenter {

    private NewChannelDisplayer newChannelDisplayer;
    private ChannelService channelService;
    private Navigator navigator;

    public NewChannelPresenter(NewChannelDisplayer newChannelDisplayer, ChannelService channelService, Navigator navigator) {
        this.newChannelDisplayer = newChannelDisplayer;
        this.channelService = channelService;
        this.navigator = navigator;
    }

    public void startPresenting() {
        newChannelDisplayer.attach(interactionListener);
        newChannelDisplayer.disableChannelCreation();
        newChannelDisplayer.disableAddingMembers();
    }

    public void stopPresenting() {
        newChannelDisplayer.detach(interactionListener);
    }

    private NewChannelDisplayer.InteractionListener interactionListener = new NewChannelDisplayer.InteractionListener() {
        @Override
        public void onChannelNameLengthChanged(int length) {
            if (length > 0) {
                newChannelDisplayer.enableChannelCreation();
            } else {
                newChannelDisplayer.disableChannelCreation();
            }
        }

        @Override
        public void onPrivateChannelSwitchStateChanged(boolean isChecked) {
            if (isChecked) {
                newChannelDisplayer.enableAddingMembers();
            } else {
                newChannelDisplayer.disableAddingMembers();
            }
        }

        @Override
        public void onCreateChannelClicked(String channelName, boolean isPrivate) {
            Channel newChannel = buildChannel(channelName, isPrivate);

            if (isPrivate) {
                channelService.createPublicChannel(newChannel); // TODO private channel
            } else {
                channelService.createPublicChannel(newChannel);
            }
            navigator.toChannels();
        }
    };

    private Channel buildChannel(String channelName, boolean isPrivate) {
        ChannelInfo channelInfo = new ChannelInfo(channelName.trim(), isPrivate);
        return new Channel(channelInfo.getName(), channelInfo);
    }
}
