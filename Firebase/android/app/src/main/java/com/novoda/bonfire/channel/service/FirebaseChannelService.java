package com.novoda.bonfire.channel.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.service.database.ChannelsDatabase;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.novoda.bonfire.rx.RxSingleValueListener.listenToSingleValueEvents;
import static com.novoda.bonfire.rx.RxValueListener.listenToValueEvents;

public class FirebaseChannelService implements ChannelService {

    private final DatabaseReference publicChannelsDB;
    private final DatabaseReference privateChannelsDB;
    private final DatabaseReference channelsDB;
    private final DatabaseReference ownersDB;
    private final DatabaseReference usersDB;

    public FirebaseChannelService(ChannelsDatabase channelsDatabase) {
        publicChannelsDB = channelsDatabase.getPublicChannelsDB();
        privateChannelsDB = channelsDatabase.getPrivateChannelsDB();
        channelsDB = channelsDatabase.getChannelsDB();
        ownersDB = channelsDatabase.getOwnersDB();
        usersDB = channelsDatabase.getUsersDB();
    }

    @Override
    public Observable<Channels> getChannelsFor(User user) {
        return Observable.combineLatest(publicChannels(), privateChannelsFor(user), mergeChannels());
    }

    private Observable<List<Channel>> publicChannels() {
        return listenToValueEvents(publicChannelsDB, getKeys())
                .flatMap(channelsFromNames());
    }

    private Observable<List<Channel>> privateChannelsFor(User user) {
        return listenToValueEvents(privateChannelsDB.child(user.getId()), getKeys())
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
                return listenToSingleValueEvents(channelsDB.child(channelName), as(Channel.class));
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
        return writeChannelToChannelsDB(newChannel)
                .flatMap(writeChannelToChannelIndexDb(newChannel))
                .onErrorReturn(DatabaseResult.<Channel>errorAsDatabaseResult());
    }

    private Observable<DatabaseResult<Channel>> writeChannelToChannelsDB(final Channel newChannel) {
        return setValue(newChannel, channelsDB.child(newChannel.getName()), new DatabaseResult<>(newChannel));
    }

    private Func1<DatabaseResult, Observable<DatabaseResult<Channel>>> writeChannelToChannelIndexDb(final Channel newChannel) {
        return new Func1<DatabaseResult, Observable<DatabaseResult<Channel>>>() {
            @Override
            public Observable<DatabaseResult<Channel>> call(DatabaseResult databaseResult) {
                return setValue(true, publicChannelsDB.child(newChannel.getName()), new DatabaseResult<>(newChannel));
            }
        };
    }

    @Override
    public Observable<DatabaseResult<Channel>> createPrivateChannel(final Channel newChannel, User owner) {
        return addUserToPrivateChannelIndex(owner, newChannel)
                .flatMap(addUserAsChannelOwner(owner))
                .flatMap(new Func1<Channel, Observable<DatabaseResult<Channel>>>() {
                    @Override
                    public Observable<DatabaseResult<Channel>> call(Channel result) {
                        return writeChannelToChannelsDB(result);
                    }
                })
                .onErrorReturn(DatabaseResult.<Channel>errorAsDatabaseResult());
    }

    @Override
    public Observable<DatabaseResult<User>> addOwnerToPrivateChannel(final Channel channel, final User newOwner) {
        return addUserToPrivateChannelIndex(newOwner, channel)
                .flatMap(addUserAsChannelOwner(newOwner))
                .map(new Func1<Channel, DatabaseResult<User>>() {
                    @Override
                    public DatabaseResult<User> call(Channel channel) {
                        return new DatabaseResult<>(newOwner); //TODO maybe not the best ?
                    }
                })
                .onErrorReturn(DatabaseResult.<User>errorAsDatabaseResult());
    }

    private Observable<Channel> addUserToPrivateChannelIndex(final User user, final Channel newChannel) {
        return setValue(true, ownersDB.child(newChannel.getName()).child(user.getId()), newChannel);
    }

    private Func1<Channel, Observable<Channel>> addUserAsChannelOwner(final User user) {
        return new Func1<Channel, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(final Channel channel) {
                return setValue(true, privateChannelsDB.child(user.getId()).child(channel.getName()), channel);
            }
        };
    }

    @Override
    public Observable<DatabaseResult<User>> removeOwnerFromPrivateChannel(final Channel channel, final User removedOwner) {
        return removeOwnerReferenceFromChannelOwners(removedOwner, channel)
                .flatMap(removeChannelReferenceFromUser(channel))
                .map(new Func1<Channel, DatabaseResult<User>>() {
                    @Override
                    public DatabaseResult<User> call(Channel channel) {
                        return new DatabaseResult<>(removedOwner); //TODO maybe not the best ?
                    }
                })
                .onErrorReturn(DatabaseResult.<User>errorAsDatabaseResult());
    }

    private Observable<DatabaseResult<User>> removeOwnerReferenceFromChannelOwners(final User user, final Channel channel) {
        return removeValue(ownersDB.child(channel.getName()).child(user.getId()), new DatabaseResult<>(user));
    }

    private Func1<DatabaseResult<User>, Observable<Channel>> removeChannelReferenceFromUser(final Channel channel) {
        return new Func1<DatabaseResult<User>, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(final DatabaseResult<User> userDatabaseResult) {
                return removeValue(privateChannelsDB.child(userDatabaseResult.getData().getId()).child(channel.getName()), channel);
            }
        };
    }

    @Override
    public Observable<DatabaseResult<Users>> getOwnersOfChannel(Channel channel) {
        return getOwnerIdsFor(channel)
                .flatMap(getUsersFromIds())
                .onErrorReturn(DatabaseResult.<Users>errorAsDatabaseResult());
    }

    private Observable<List<String>> getOwnerIdsFor(final Channel channel) {
        return listenToValueEvents(ownersDB.child(channel.getName()), getKeys());
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
                return listenToSingleValueEvents(usersDB.child(userId), as(User.class));
            }
        };
    }

    private <T, U> Observable<U> setValue(final T value, final DatabaseReference databaseReference, final U returnValue) {
        return Observable.create(new Observable.OnSubscribe<U>() {
            @Override
            public void call(Subscriber<? super U> subscriber) {
                databaseReference.setValue(value, new RxCompletionListener<>(subscriber, returnValue));
            }
        });
    }

    private <T> Observable<T> removeValue(final DatabaseReference databaseReference, final T returnValue) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                databaseReference.removeValue(new RxCompletionListener<>(subscriber, returnValue));
            }
        });
    }

    private static class RxCompletionListener<T> implements DatabaseReference.CompletionListener {

        private final Subscriber<? super T> subscriber;
        private final T successValue;

        private RxCompletionListener(Subscriber<? super T> subscriber, T successValue) {
            this.subscriber = subscriber;
            this.successValue = successValue;
        }

        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            if (databaseError == null) {
                subscriber.onNext(successValue);
                subscriber.onCompleted();
            } else {
                subscriber.onError(databaseError.toException());
            }
        }

    }

    private Func1<DataSnapshot, List<String>> getKeys() {
        return new Func1<DataSnapshot, List<String>>() {
            @Override
            public List<String> call(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot child : children) {
                        keys.add(child.getKey());
                    }
                }
                return keys;
            }
        };
    }

    private <T> Func1<DataSnapshot, T> as(final Class<T> tClass) {
        return new Func1<DataSnapshot, T>() {
            @Override
            public T call(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(tClass);
            }
        };
    }
}
