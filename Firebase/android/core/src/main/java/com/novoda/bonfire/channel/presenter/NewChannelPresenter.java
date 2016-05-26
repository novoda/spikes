package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class NewChannelPresenter {

    private final NewChannelDisplayer newChannelDisplayer;
    private final ChannelService channelService;
    private final LoginService loginService;
    private final Navigator navigator;
    private User user;
    private CompositeSubscription subscriptions = new CompositeSubscription();

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
        newChannelDisplayer.attach(channelCreationListener);
        subscriptions.add(
                loginService.getAuthentication().subscribe(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
                        user = authentication.getUser();
                    }
                })
        );
    }

    public void stopPresenting() {
        newChannelDisplayer.detach(channelCreationListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private NewChannelDisplayer.ChannelCreationListener channelCreationListener = new NewChannelDisplayer.ChannelCreationListener() {

        @Override
        public void onCreateChannelClicked(String channelName, boolean isPrivate) {
            Channel newChannel = new Channel(channelName.trim(), isPrivate);
            subscriptions.add(
                    create(newChannel).subscribe(new Action1<DatabaseResult<Channel>>() {
                        @Override
                        public void call(DatabaseResult<Channel> databaseResult) {
                            if (databaseResult.isSuccess()) {
                                navigator.toChannel(databaseResult.getData());
                            } else {
                                newChannelDisplayer.showChannelCreationError();
                            }
                        }
                    })
            );
        }
    };

    private Observable<DatabaseResult<Channel>> create(Channel newChannel) {
        if (newChannel.isPrivate()) {
            return channelService.createPrivateChannel(newChannel, user);
        } else {
            return channelService.createPublicChannel(newChannel);
        }
    }

}
