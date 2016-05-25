package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;

import rx.functions.Action1;

public class NewChannelPresenter {

    private final NewChannelDisplayer newChannelDisplayer;
    private final ChannelService channelService;
    private final LoginService loginService;
    private final Navigator navigator;
    private User user;

    public NewChannelPresenter(NewChannelDisplayer newChannelDisplayer,
                               ChannelService channelService,
                               LoginService loginService,
                               Navigator navigator) {
        this.newChannelDisplayer = newChannelDisplayer;
        this.channelService = channelService;
        this.loginService = loginService;
        this.navigator = navigator;
    }

    public void startPresenting() {
        newChannelDisplayer.attach(interactionListener);
        newChannelDisplayer.disableChannelCreation();
        loginService.getAuthentication().subscribe(new Action1<Authentication>() {
            @Override
            public void call(Authentication authentication) {
                user = authentication.getUser();
            }
        });
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
        public void onCreateChannelClicked(String channelName, boolean isPrivate) {
            final Channel newChannel = buildChannel(channelName, isPrivate);
            if (isPrivate) {
                channelService.createPrivateChannel(newChannel, user)
                        .subscribe(new Action1<DatabaseResult<Channel>>() {
                            @Override
                            public void call(DatabaseResult<Channel> databaseResult) {
                                if (databaseResult.isSuccess()) {
                                    navigator.toAddUsersFor(databaseResult.getData());
                                } else {
                                    newChannelDisplayer.showChannelCreationError();
                                }
                            }
                        });
            } else {
                channelService.createPublicChannel(newChannel)
                        .subscribe(new Action1<DatabaseResult<Channel>>() {
                            @Override
                            public void call(DatabaseResult<Channel> databaseResult) {
                                if (databaseResult.isSuccess()) {
                                    navigator.toChannel(databaseResult.getData());
                                } else {
                                    newChannelDisplayer.showChannelCreationError();
                                }
                            }
                        });
            }
        }
    };

    private Channel buildChannel(String channelName, boolean isPrivate) {
        return new Channel(channelName.trim(), isPrivate);
    }
}
