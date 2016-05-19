package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelInfo;
import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.navigation.Navigator;

import rx.Observable;
import rx.functions.Action1;

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
            Observable<ChannelWriteResult> resultObservable;
            if (isPrivate) {
                resultObservable = channelService.createPublicChannel(newChannel); // TODO private channel
            } else {
                resultObservable = channelService.createPublicChannel(newChannel);
            }
            resultObservable.subscribe(new Action1<ChannelWriteResult>() {
                @Override
                public void call(ChannelWriteResult channelWriteResult) {
                    if (channelWriteResult.isFailure()) {
                        newChannelDisplayer.showChannelCreationError();
                    } else {
                        navigator.toChannels();
                    }
                }
            });
        }
    };

    private Channel buildChannel(String channelName, boolean isPrivate) {
        ChannelInfo channelInfo = new ChannelInfo(channelName.trim(), isPrivate);
        return new Channel(channelInfo.getName(), channelInfo);
    }
}
