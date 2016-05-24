package com.novoda.bonfire.user.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.displayer.UsersDisplayer;
import com.novoda.bonfire.user.service.UserService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class UsersPresenter {
    private final UserService userService;
    private final ChannelService channelService;
    private final UsersDisplayer usersDisplayer;
    private final Channel channel;
    private final Navigator navigator;

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
        channelService.getOwnersOfChannel(channel)
                .flatMap(getSelectedUsersData())
                .subscribe(updateSelectedUsers());
    }

    public void stopPresenting() {
        usersDisplayer.detach(selectionListener);
    }
    private UsersDisplayer.SelectionListener selectionListener = new UsersDisplayer.SelectionListener() {
        @Override
        public void onUserSelected(User user) {
            channelService.addOwnerToPrivateChannel(channel, user)
                    .flatMap(getSelectedUsersData())
                    .subscribe(updateSelectedUsers());
        }

        @Override
        public void onUserDeselected(User user) {
            channelService.removeOwnerFromPrivateChannel(channel, user)
                    .flatMap(getSelectedUsersData())
                    .subscribe(updateSelectedUsers());
        }

        @Override
        public void onCompleteClicked() {
            navigator.toChannel(channel);
        }
    };

    private Action1<DatabaseResult<Users>> updateSelectedUsers() {
        return new Action1<DatabaseResult<Users>>() {
            @Override
            public void call(DatabaseResult<Users> databaseResult) {
                if (databaseResult.isSuccess()) {
                    usersDisplayer.displaySelectedUsers(databaseResult.getData());
                } else {
                    usersDisplayer.showFailure();
                }
            }
        };
    }

    private Func1<DatabaseResult<List<String>>, Observable<DatabaseResult<Users>>> getSelectedUsersData() {
        return new Func1<DatabaseResult<List<String>>, Observable<DatabaseResult<Users>>>() {
            @Override
            public Observable<DatabaseResult<Users>> call(final DatabaseResult<List<String>> result) {
                if (result.isSuccess()) {
                    return userService.getUsersForIds(result.getData());
                } else {
                    return Observable.create(new Observable.OnSubscribe<DatabaseResult<Users>>() {
                        @Override
                        public void call(Subscriber<? super DatabaseResult<Users>> subscriber) {
                            subscriber.onNext(new DatabaseResult<Users>(result.getFailure()));
                        }
                    });
                }
            }
        };
    }
}
