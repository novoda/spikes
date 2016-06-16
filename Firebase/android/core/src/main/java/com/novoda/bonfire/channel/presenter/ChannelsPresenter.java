package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.Config;
import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.link.LinkFactory;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class ChannelsPresenter {

    private final ChannelsDisplayer channelsDisplayer;
    private final ChannelService channelService;
    private final LoginService loginService;
    private final Config config;
    private final Navigator navigator;
    private final LinkFactory linkFactory;
    private final Analytics analytics;

    private Subscription subscription;
    private User user;

    public ChannelsPresenter(
            ChannelsDisplayer channelsDisplayer,
            ChannelService channelService,
            LoginService loginService,
            Config config,
            Navigator navigator,
            LinkFactory linkFactory,
            Analytics analytics
    ) {
        this.channelsDisplayer = channelsDisplayer;
        this.channelService = channelService;
        this.loginService = loginService;
        this.config = config;
        this.navigator = navigator;
        this.linkFactory = linkFactory;
        this.analytics = analytics;
    }

    public void startPresenting() {
        channelsDisplayer.attach(channelsInteractionListener);
        subscription = loginService.getAuthentication()
                .filter(successfullyAuthenticated())
                .doOnNext(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
                        user = authentication.getUser();
                    }
                })
                .flatMap(channelsForUser())
                .map(sortIfConfigured())
                .subscribe(new Action1<Channels>() {
                    @Override
                    public void call(Channels channels) {
                        channelsDisplayer.display(channels);
                    }
                });
    }

    private Func1<Authentication, Observable<Channels>> channelsForUser() {
        return new Func1<Authentication, Observable<Channels>>() {
            @Override
            public Observable<Channels> call(Authentication authentication) {
                return channelService.getChannelsFor(authentication.getUser());
            }
        };
    }

    private Func1<Channels, Channels> sortIfConfigured() {
        return new Func1<Channels, Channels>() {
            @Override
            public Channels call(Channels channels) {
                if (config.orderChannelsByName()) {
                    return channels.sortedByName();
                } else {
                    return channels;
                }
            }
        };
    }

    private Func1<Authentication, Boolean> successfullyAuthenticated() {
        return new Func1<Authentication, Boolean>() {
            @Override
            public Boolean call(Authentication authentication) {
                return authentication.isSuccess();
            }
        };
    }

    public void stopPresenting() {
        subscription.unsubscribe();
        channelsDisplayer.detach(channelsInteractionListener);
    }

    private final ChannelsDisplayer.ChannelsInteractionListener channelsInteractionListener = new ChannelsDisplayer.ChannelsInteractionListener() {
        @Override
        public void onChannelSelected(Channel channel) {
            analytics.trackSelectChannel(channel.getName());
            navigator.toChannel(channel);
        }

        @Override
        public void onAddNewChannel() {
            analytics.trackCreateChannel(user.getId());
            navigator.toCreateChannel();
        }

        @Override
        public void onInviteUsersClicked() {
            analytics.trackSendInvitesSelected(user.getId());
            navigator.toShareInvite(linkFactory.inviteLinkFrom(user).toString());
        }
    };
}
