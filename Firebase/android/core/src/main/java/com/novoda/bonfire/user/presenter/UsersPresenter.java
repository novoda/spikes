package com.novoda.bonfire.user.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.displayer.UsersDisplayer;
import com.novoda.bonfire.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class UsersPresenter {
    private final UserService userService;
    private final ChannelService channelService;
    private final UsersDisplayer usersDisplayer;
    private final Channel channel;
    private final Navigator navigator;
    private final List<User> newOwners = new ArrayList<>();

    public UsersPresenter(UserService userService, ChannelService channelService, UsersDisplayer usersDisplayer, Channel channel, Navigator navigator) {
        this.userService = userService;
        this.channelService = channelService;
        this.usersDisplayer = usersDisplayer;
        this.channel = channel;
        this.navigator = navigator;
    }

    public void startPresenting() {
        usersDisplayer.attach(selectionListener);
        userService.getAllUsers().subscribe(new Action1<Users>() {
            @Override
            public void call(Users users) {
                usersDisplayer.display(users);
            }
        });
    }

    public void stopPresenting() {
        usersDisplayer.detach(selectionListener);
    }
    private UsersDisplayer.SelectionListener selectionListener = new UsersDisplayer.SelectionListener() {
        @Override
        public void onUserSelected(User user) {
            newOwners.add(user);
        }

        @Override
        public void onCompleteClicked() {
            channelService.addOwnersToPrivateChannel(channel, newOwners)
                    .subscribe(new Action1<ChannelWriteResult>() {
                        @Override
                        public void call(ChannelWriteResult channelWriteResult) {
                            if (channelWriteResult.isFailure()) {
                                usersDisplayer.showFailure();
                            } else {
                                navigator.toChannel(channel);
                            }
                        }
                    });
        }
    };
}
