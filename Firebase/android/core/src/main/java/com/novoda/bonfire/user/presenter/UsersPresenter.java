package com.novoda.bonfire.user.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.displayer.UsersDisplayer;
import com.novoda.bonfire.user.service.UserService;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class UsersPresenter {
    private final UserService userService;
    private final ChannelService channelService;
    private final UsersDisplayer usersDisplayer;
    private final Channel channel;
    private final Navigator navigator;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public UsersPresenter(UserService userService, ChannelService channelService, UsersDisplayer usersDisplayer, Channel channel, Navigator navigator) {
        this.userService = userService;
        this.channelService = channelService;
        this.usersDisplayer = usersDisplayer;
        this.channel = channel;
        this.navigator = navigator;
    }

    public void startPresenting() {
        usersDisplayer.attach(selectionListener);

        subscriptions.add(
                userService.getAllUsers().subscribe(new Action1<Users>() {
                    @Override
                    public void call(Users users) {
                        usersDisplayer.display(users);
                    }
                })
        );
        subscriptions.add(
                channelService.getOwnersOfChannel(channel)
                        .subscribe(new Action1<DatabaseResult<Users>>() {
                            @Override
                            public void call(DatabaseResult<Users> databaseResult) {
                                if (databaseResult.isSuccess()) {
                                    usersDisplayer.displaySelectedUsers(databaseResult.getData());
                                } else {
                                    usersDisplayer.showFailure();
                                }
                            }
                        })
        );
    }

    public void stopPresenting() {
        usersDisplayer.detach(selectionListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private UsersDisplayer.SelectionListener selectionListener = new UsersDisplayer.SelectionListener() {
        @Override
        public void onUserSelected(final User user) {
            channelService.addOwnerToPrivateChannel(channel, user)
                    .subscribe(updateOnActionResult());
        }

        @Override
        public void onUserDeselected(User user) {
            channelService.removeOwnerFromPrivateChannel(channel, user)
                    .subscribe(updateOnActionResult());
        }

        @Override
        public void onCompleteClicked() {
            navigator.toParent();
        }
    };

    private Action1<DatabaseResult<User>> updateOnActionResult() {
        return new Action1<DatabaseResult<User>>() {
            @Override
            public void call(DatabaseResult<User> userDatabaseResult) {
                if (!userDatabaseResult.isSuccess()) {
                    usersDisplayer.showFailure();
                }
            }
        };
    }

}
