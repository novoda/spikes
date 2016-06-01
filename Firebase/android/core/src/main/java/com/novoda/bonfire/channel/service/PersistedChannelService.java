package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.database.ChannelsDatabase;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class PersistedChannelService implements ChannelService {

    private final ChannelsDatabase channelsDatabase;
    private final UserDatabase userDatabase;

    public PersistedChannelService(ChannelsDatabase channelsDatabase, UserDatabase userDatabase) {
        this.channelsDatabase = channelsDatabase;
        this.userDatabase = userDatabase;
    }

    @Override
    public Observable<Channels> getChannelsFor(User user) {
        return Observable.combineLatest(publicChannels(), privateChannelsFor(user), mergeChannels());
    }

    private Observable<List<Channel>> publicChannels() {
        return channelsDatabase.observePublicChannelIds()
                .flatMap(channelsFromNames());
    }

    private Observable<List<Channel>> privateChannelsFor(User user) {
        return channelsDatabase.observePrivateChannelIdsFor(user)
                .flatMap(channelsFromNames());
    }

    private Func1<List<String>, Observable<List<Channel>>> channelsFromNames() {
        return new Func1<List<String>, Observable<List<Channel>>>() {
            @Override
            public Observable<List<Channel>> call(List<String> channelNames) {
                return Observable.from(channelNames)
                        .flatMap(channelFromName())
                        .toList();
            }
        };
    }

    private Func1<String, Observable<Channel>> channelFromName() {
        return new Func1<String, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(final String channelName) {
                return channelsDatabase.readChannelFor(channelName);
            }
        };
    }

    private Func2<List<Channel>, List<Channel>, Channels> mergeChannels() {
        return new Func2<List<Channel>, List<Channel>, Channels>() {
            @Override
            public Channels call(List<Channel> channels, List<Channel> channels2) {
                List<Channel> mergedChannels = new ArrayList<>(channels);
                mergedChannels.addAll(channels2);
                return new Channels(mergedChannels);
            }
        };
    }

    @Override
    public Observable<DatabaseResult<Channel>> createPublicChannel(Channel newChannel) {
        return channelsDatabase.writeChannel(newChannel)
                .flatMap(writeChannelToChannelIndexDb())
                .onErrorReturn(DatabaseResult.<Channel>errorAsDatabaseResult());
    }

    private Func1<Channel, Observable<DatabaseResult<Channel>>> writeChannelToChannelIndexDb() {
        return new Func1<Channel, Observable<DatabaseResult<Channel>>>() {
            @Override
            public Observable<DatabaseResult<Channel>> call(Channel channel) {
                return channelsDatabase.writeChannelToPublicChannelIndex(channel)
                        .map(new Func1<Channel, DatabaseResult<Channel>>() {
                            @Override
                            public DatabaseResult<Channel> call(Channel channel) {
                                return new DatabaseResult<>(channel);
                            }
                        });
            }
        };
    }

    @Override
    public Observable<DatabaseResult<Channel>> createPrivateChannel(final Channel newChannel, User owner) {
        return channelsDatabase.addOwnerToPrivateChannel(owner, newChannel)
                .flatMap(addUserAsChannelOwner(owner))
                .flatMap(writeChannel())
                .onErrorReturn(DatabaseResult.<Channel>errorAsDatabaseResult());
    }

    private Func1<Channel, Observable<DatabaseResult<Channel>>> writeChannel() {
        return new Func1<Channel, Observable<DatabaseResult<Channel>>>() {
            @Override
            public Observable<DatabaseResult<Channel>> call(Channel result) {
                return channelsDatabase.writeChannel(result)
                        .map(new Func1<Channel, DatabaseResult<Channel>>() {
                            @Override
                            public DatabaseResult<Channel> call(Channel channel) {
                                return new DatabaseResult<>(channel);
                            }
                        });
            }
        };
    }

    @Override
    public Observable<DatabaseResult<User>> addOwnerToPrivateChannel(final Channel channel, final User newOwner) {
        return channelsDatabase.addOwnerToPrivateChannel(newOwner, channel)
                .flatMap(addUserAsChannelOwner(newOwner))
                .map(new Func1<Channel, DatabaseResult<User>>() {
                    @Override
                    public DatabaseResult<User> call(Channel channel) {
                        return new DatabaseResult<>(newOwner); //TODO maybe not the best ?
                    }
                })
                .onErrorReturn(DatabaseResult.<User>errorAsDatabaseResult());
    }

    private Func1<Channel, Observable<Channel>> addUserAsChannelOwner(final User user) {
        return new Func1<Channel, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(final Channel channel) {
                return channelsDatabase.addChannelToUserPrivateChannelIndex(user, channel);
            }
        };
    }

    @Override
    public Observable<DatabaseResult<User>> removeOwnerFromPrivateChannel(final Channel channel, final User removedOwner) {
        return channelsDatabase.removeOwnerFromPrivateChannel(removedOwner, channel)
                .flatMap(removeChannelReferenceFromUser(removedOwner))
                .map(new Func1<Channel, DatabaseResult<User>>() {
                    @Override
                    public DatabaseResult<User> call(Channel channel) {
                        return new DatabaseResult<>(removedOwner); //TODO maybe not the best ?
                    }
                })
                .onErrorReturn(DatabaseResult.<User>errorAsDatabaseResult());
    }

    private Func1<Channel, Observable<Channel>> removeChannelReferenceFromUser(final User user) {
        return new Func1<Channel, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(Channel channel) {
                return channelsDatabase.removeChannelFromUserPrivateChannelIndex(user, channel);
            }
        };
    }

    @Override
    public Observable<DatabaseResult<Users>> getOwnersOfChannel(Channel channel) {
        return channelsDatabase.observeOwnerIdsFor(channel)
                .flatMap(getUsersFromIds())
                .onErrorReturn(DatabaseResult.<Users>errorAsDatabaseResult());
    }

    private Func1<List<String>, Observable<DatabaseResult<Users>>> getUsersFromIds() {
        return new Func1<List<String>, Observable<DatabaseResult<Users>>>() {
            @Override
            public Observable<DatabaseResult<Users>> call(List<String> userIds) {
                return Observable.from(userIds)
                        .flatMap(getUserFromId())
                        .toList()
                        .map(new Func1<List<User>, DatabaseResult<Users>>() {
                            @Override
                            public DatabaseResult<Users> call(List<User> users) {
                                return new DatabaseResult<>(new Users(users));
                            }
                        });
            }
        };
    }

    private Func1<String, Observable<User>> getUserFromId() {
        return new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String userId) {
                return userDatabase.readUserFrom(userId);
            }
        };
    }
}
