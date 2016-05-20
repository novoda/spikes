package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelInfo;
import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.channel.data.model.UserSearchResult;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.channel.service.UserService;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.data.model.User;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.Navigator;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class NewChannelPresenter {

    private final NewChannelDisplayer newChannelDisplayer;
    private final ChannelService channelService;
    private final LoginService loginService;
    private final UserService userService;
    private final Navigator navigator;
    private final List<User> owners = new ArrayList<>();
    private User user;

    public NewChannelPresenter(NewChannelDisplayer newChannelDisplayer,
                               ChannelService channelService,
                               LoginService loginService,
                               UserService userService,
                               Navigator navigator) {
        this.newChannelDisplayer = newChannelDisplayer;
        this.channelService = channelService;
        this.loginService = loginService;
        this.userService = userService;
        this.navigator = navigator;
    }

    public void startPresenting() {
        newChannelDisplayer.attach(interactionListener);
        newChannelDisplayer.disableChannelCreation();
        newChannelDisplayer.disableAddingMembers();
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
        public void onPrivateChannelSwitchStateChanged(boolean isChecked) {
            if (isChecked) {
                newChannelDisplayer.enableAddingMembers();
            } else {
                newChannelDisplayer.disableAddingMembers();
            }
        }

        @Override
        public void onAddOwner(String name) {
            userService.getUserWithName(name).filter(new Func1<UserSearchResult, Boolean>() {
                @Override
                public Boolean call(UserSearchResult userSearchResult) {
                    return userSearchResult.isSuccess();
                }
            }).subscribe(new Action1<UserSearchResult>() {
                @Override
                public void call(UserSearchResult user) {
                    owners.add(user.getUser());
                }
            });
        }

        @Override
        public void onCreateChannelClicked(String channelName, boolean isPrivate) {
            final Channel newChannel = buildChannel(channelName, isPrivate);
            Observable<ChannelWriteResult> resultObservable;
            if (isPrivate) {
                resultObservable = channelService.createPrivateChannel(newChannel, user)
                        .flatMap(new Func1<ChannelWriteResult, Observable<ChannelWriteResult>>() {
                            @Override
                            public Observable<ChannelWriteResult> call(ChannelWriteResult channelWriteResult) {
                                if (channelWriteResult.isFailure()) {
                                    return Observable.just(channelWriteResult);
                                }
                                return channelService.addOwnersToPrivateChannel(newChannel, owners);
                            }
                        });
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
