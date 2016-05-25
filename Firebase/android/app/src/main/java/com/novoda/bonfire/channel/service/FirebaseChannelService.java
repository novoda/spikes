package com.novoda.bonfire.channel.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.BooleanSubscription;

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
        return Observable.create(channelNames(publicChannelsDB))
                .flatMap(channelsFromNames());
    }

    private Observable<List<Channel>> privateChannelsFor(User user) {
        return Observable.create(channelNames(privateChannelsDB.child(user.getId())))
                .flatMap(channelsFromNames());
    }

    private Observable.OnSubscribe<List<String>> channelNames(final DatabaseReference indexChannelsDB) {
        return new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                final ValueEventListener eventListener = indexChannelsDB.addValueEventListener(new ChannelNamesEventListener(subscriber));
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        indexChannelsDB.removeEventListener(eventListener);
                    }
                }));
            }
        };
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
                return Observable.create(new Observable.OnSubscribe<Channel>() {
                    @Override
                    public void call(Subscriber<? super Channel> subscriber) {
                        channelsDB.child(channelName).addListenerForSingleValueEvent(new ChannelEventListener(subscriber));
                    }
                });
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
                .onErrorReturn(this.<Channel>errorConvertedToWriteResult());
    }

    private Observable<DatabaseResult<Channel>> writeChannelToChannelsDB(final Channel newChannel) {
        return Observable.create(new Observable.OnSubscribe<DatabaseResult<Channel>>() {
            @Override
            public void call(final Subscriber<? super DatabaseResult<Channel>> subscriber) {
                channelsDB.child(newChannel.getName()).setValue(newChannel, completionListenerFor(subscriber, newChannel));
            }
        });
    }

    private Func1<DatabaseResult, Observable<DatabaseResult<Channel>>> writeChannelToChannelIndexDb(final Channel newChannel) {
        return new Func1<DatabaseResult, Observable<DatabaseResult<Channel>>>() {
            @Override
            public Observable<DatabaseResult<Channel>> call(DatabaseResult databaseResult) {
                return Observable.create(new Observable.OnSubscribe<DatabaseResult<Channel>>() {
                    @Override
                    public void call(final Subscriber<? super DatabaseResult<Channel>> subscriber) {
                        publicChannelsDB.child(newChannel.getName()).setValue(true, completionListenerFor(subscriber, newChannel));
                    }
                });
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
                .onErrorReturn(this.<Channel>errorConvertedToWriteResult());
    }

    @Override
    public Observable<DatabaseResult<User>> addOwnerToPrivateChannel(final Channel channel, final User newOwner) {
        return addUserToPrivateChannelIndex(newOwner, channel)
                .flatMap(addUserAsChannelOwner(newOwner))
                .map(new Func1<Channel, DatabaseResult<User>>() {
                    @Override
                    public DatabaseResult<User> call(Channel channel) {
                        return new DatabaseResult<User>(newOwner); //TODO maybe not the best ?
                    }
                })
                .onErrorReturn(this.<User>errorConvertedToWriteResult());
    }

    private Observable<Channel> addUserToPrivateChannelIndex(final User user, final Channel newChannel) {
        return Observable.create(new Observable.OnSubscribe<Channel>() {
            @Override
            public void call(final Subscriber<? super Channel> subscriber) {
                DatabaseReference channelRefInOwners = ownersDB.child(newChannel.getName()).child(user.getId());
                channelRefInOwners.setValue(true, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            subscriber.onNext(newChannel);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(databaseError.toException());
                        }
                    }
                });
            }
        });
    }

    private Func1<Channel, Observable<Channel>> addUserAsChannelOwner(final User user) {
        return new Func1<Channel, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(final Channel channel) {
                return Observable.create(new Observable.OnSubscribe<Channel>() {
                    @Override
                    public void call(final Subscriber<? super Channel> subscriber) {
                        DatabaseReference channelReference = privateChannelsDB.child(user.getId()).child(user.getName());
                        channelReference.setValue(true, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    subscriber.onNext(channel);
                                    subscriber.onCompleted();
                                } else {
                                    subscriber.onError(databaseError.toException());
                                }
                            }
                        });
                    }
                });
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
                        return new DatabaseResult<User>(removedOwner); //TODO maybe not the best ?
                    }
                })
                .onErrorReturn(this.<User>errorConvertedToWriteResult());
    }

    private Observable<DatabaseResult<User>> removeOwnerReferenceFromChannelOwners(final User user, final Channel channel) {
        return Observable.create(new Observable.OnSubscribe<DatabaseResult<User>>() {
            @Override
            public void call(final Subscriber<? super DatabaseResult<User>> subscriber) {
                DatabaseReference channelRefInOwners = ownersDB.child(channel.getName()).child(user.getId());
                channelRefInOwners.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            subscriber.onNext(new DatabaseResult<>(user));
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(databaseError.toException());
                        }
                    }
                });
            }
        });
    }

    private Func1<DatabaseResult<User>, Observable<Channel>> removeChannelReferenceFromUser(final Channel channel) {
        return new Func1<DatabaseResult<User>, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(final DatabaseResult<User> userDatabaseResult) {

                return Observable.create(new Observable.OnSubscribe<Channel>() {
                    @Override
                    public void call(final Subscriber<? super Channel> subscriber) {
                        DatabaseReference channelReference = privateChannelsDB.child(userDatabaseResult.getData().getId()).child(channel.getName());
                        channelReference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    subscriber.onNext(channel);
                                    subscriber.onCompleted();
                                } else {
                                    subscriber.onError(databaseError.toException());
                                }
                            }
                        });
                    }
                });
            }
        };
    }

    @Override
    public Observable<DatabaseResult<Users>> getOwnersOfChannel(Channel channel) {
        return getOwnerIdsFor(channel)
                .flatMap(getUsersFromIds())
                .onErrorReturn(this.<Users>errorConvertedToWriteResult());
    }

    private Observable<List<String>> getOwnerIdsFor(final Channel channel) {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(final Subscriber<? super List<String>> subscriber) {
                final ValueEventListener eventListener = ownersDB.child(channel.getName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(getKeys(dataSnapshot));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(databaseError.toException());
                    }
                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        ownersDB.removeEventListener(eventListener);
                    }
                }));
            }
        });
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
                                return new DatabaseResult<Users>(new Users(users));
                            }
                        });
            }
        };
    }

    private Func1<String, Observable<User>> getUserFromId() {
        return new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String userId) {
                return Observable.create(new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(final Subscriber<? super User> subscriber) {
                        usersDB.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    subscriber.onNext(dataSnapshot.getValue(User.class));
                                }
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                subscriber.onError(databaseError.toException());
                            }
                        });
                    }
                });
            }
        };
    }

    private <T> Func1<Throwable, DatabaseResult<T>> errorConvertedToWriteResult() {
        return new Func1<Throwable, DatabaseResult<T>>() {
            @Override
            public DatabaseResult<T> call(Throwable throwable) {
                return new DatabaseResult<>(throwable == null ? new DatabaseException("Database error is missing") : throwable);
            }
        };
    }

    private DatabaseReference.CompletionListener completionListenerFor(final Subscriber<? super DatabaseResult<Channel>> subscriber, final Channel channel) {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    subscriber.onNext(new DatabaseResult<>(channel));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(databaseError.toException());
                }
            }
        };
    }

    private List<String> getKeys(DataSnapshot dataSnapshot) {
        List<String> channelPaths = new ArrayList<>();
        if (dataSnapshot.hasChildren()) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            for (DataSnapshot child : children) {
                String path = child.getKey();
                channelPaths.add(path);
            }
        }
        return channelPaths;
    }

    private class ChannelNamesEventListener implements ValueEventListener {

        private final Subscriber<? super List<String>> subscriber;

        private ChannelNamesEventListener(Subscriber<? super List<String>> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(getKeys(dataSnapshot));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }

    }

    private class ChannelEventListener implements ValueEventListener {
        private final Subscriber<? super Channel> subscriber;

        public ChannelEventListener(Subscriber<? super Channel> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                subscriber.onNext(dataSnapshot.getValue(Channel.class));
            }
            subscriber.onCompleted();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }
    }
}
