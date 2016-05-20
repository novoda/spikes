package com.novoda.bonfire.channel.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelInfo;
import com.novoda.bonfire.channel.data.model.ChannelWriteResult;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.login.data.model.User;

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

    public FirebaseChannelService(FirebaseDatabase firebaseDatabase) {
        publicChannelsDB = firebaseDatabase.getReference("public-channels-index");
        privateChannelsDB = firebaseDatabase.getReference("private-channels-index");
        channelsDB = firebaseDatabase.getReference("channels");
        ownersDB = firebaseDatabase.getReference("owners");
    }

    @Override
    public Observable<Channels> getChannelsFor(User user) {
        return Observable.zip(privateChannelsFor(user), publicChannels(), mergeChannelLists())
                .flatMap(addChannelInfo())
                .flatMap(convertToChannelsInstance());
    }

    @Override
    public Observable<ChannelWriteResult> createPublicChannel(Channel newChannel) {
        return writeChannelToChannelsDB(newChannel)
                .flatMap(writeChannelToChannelIndexDb(newChannel))
                .onErrorReturn(errorConertedToWriteResult());
    }

    @Override
    public Observable<ChannelWriteResult> createPrivateChannel(final Channel newChannel, User owner) {
        return Observable.just(owner)
                .flatMap(addUserToPrivateChannelIndex(newChannel))
                .flatMap(addUserAsChannelOwner(newChannel))
                .flatMap(new Func1<ChannelWriteResult, Observable<ChannelWriteResult>>() {
                    @Override
                    public Observable<ChannelWriteResult> call(ChannelWriteResult user) {
                        return writeChannelToChannelsDB(newChannel);
                    }
                })
                .onErrorReturn(errorConertedToWriteResult());
    }

    @Override
    public Observable<ChannelWriteResult> addOwnersToPrivateChannel(Channel privateChannel, List<User> newOwners) {
        return Observable.from(newOwners)
                .flatMap(addUserToPrivateChannelIndex(privateChannel))
                .flatMap(addUserAsChannelOwner(privateChannel))
                .onErrorReturn(errorConertedToWriteResult());
    }

    @NonNull
    private Func1<User, Observable<ChannelWriteResult>> addUserAsChannelOwner(final Channel newChannel) {
        return new Func1<User, Observable<ChannelWriteResult>>() {
            @Override
            public Observable<ChannelWriteResult> call(final User user) {
                return Observable.create(new Observable.OnSubscribe<ChannelWriteResult>() {
                    @Override
                    public void call(final Subscriber<? super ChannelWriteResult> subscriber) {
                        DatabaseReference channelReference = privateChannelsDB.child(user.getId());
                        channelReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                subscriber.onNext(new ChannelWriteResult());
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                subscriber.onError(databaseError.toException());
                            }
                        });
                        channelReference.child(newChannel.getPath()).setValue(true);
                    }
                });
            }
        };
    }

    @NonNull
    private Func1<User, Observable<User>> addUserToPrivateChannelIndex(final Channel newChannel) {
        return new Func1<User, Observable<User>>() {
            @Override
            public Observable<User> call(final User user) {
                return Observable.create(new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(final Subscriber<? super User> subscriber) {
                        DatabaseReference channelRefInOwners = ownersDB.child(newChannel.getPath());
                        channelRefInOwners.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                subscriber.onNext(user);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                subscriber.onError(databaseError.toException());
                            }
                        });
                        channelRefInOwners.child(user.getId()).setValue(true);
                    }
                });
            }
        };
    }

    @NonNull
    private Observable<ChannelWriteResult> observableWithLoops(final Channel newChannel, final List<User> owners) {
        return Observable.create(new Observable.OnSubscribe<ChannelWriteResult>() {
            @Override
            public void call(final Subscriber<? super ChannelWriteResult> subscriber) {
                DatabaseReference channelReference = ownersDB.child(newChannel.getPath());
                final int ownerCount = owners.size();
                ValueEventListener eventListener = channelReference.addValueEventListener(new ValueEventListener() {
                    int receivedEventCount = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        receivedEventCount++;
                        if (receivedEventCount == ownerCount) {
                            subscriber.onNext(new ChannelWriteResult());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(databaseError.toException());
                    }
                });

                removeEventListenerFromDatabase(channelReference, eventListener);

                for (User user : owners) {
                    channelReference.child(user.getId()).setValue(true);
                }


            }
        }).flatMap(new Func1<ChannelWriteResult, Observable<ChannelWriteResult>>() {
            @Override
            public Observable<ChannelWriteResult> call(ChannelWriteResult channelWriteResult) {
                return Observable.create(new Observable.OnSubscribe<ChannelWriteResult>() {
                    @Override
                    public void call(final Subscriber<? super ChannelWriteResult> subscriber) {
                        final int ownerCount = owners.size();
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            int receivedEventCount = 0;

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                receivedEventCount++;
                                if (receivedEventCount == ownerCount) {
                                    subscriber.onNext(new ChannelWriteResult());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        for (User user : owners) {
                            DatabaseReference channelReference = privateChannelsDB.child(user.getId());
                            channelReference.addListenerForSingleValueEvent(valueEventListener);
                            channelReference.child(newChannel.getPath()).setValue(true);
                        }
                    }
                });
            }
        }).flatMap(new Func1<ChannelWriteResult, Observable<ChannelWriteResult>>() {
            @Override
            public Observable<ChannelWriteResult> call(ChannelWriteResult channelWriteResult) {
                return writeChannelToChannelsDB(newChannel);
            }
        }).onErrorReturn(errorConertedToWriteResult());
    }

    @NonNull
    private Observable<ChannelWriteResult> writeChannelToChannelsDB(final Channel newChannel) {
        return Observable.create(new Observable.OnSubscribe<ChannelWriteResult>() {
            @Override
            public void call(final Subscriber<? super ChannelWriteResult> subscriber) {
                channelsDB.child(newChannel.getPath()).setValue(newChannel.getChannelInfo(), completionListenerFor(subscriber));
            }
        });
    }

    @NonNull
    private Func1<ChannelWriteResult, Observable<ChannelWriteResult>> writeChannelToChannelIndexDb(final Channel newChannel) {
        return new Func1<ChannelWriteResult, Observable<ChannelWriteResult>>() {
            @Override
            public Observable<ChannelWriteResult> call(ChannelWriteResult channelWriteResult) {
                return Observable.create(new Observable.OnSubscribe<ChannelWriteResult>() {
                    @Override
                    public void call(final Subscriber<? super ChannelWriteResult> subscriber) {
                        publicChannelsDB.child(newChannel.getPath()).setValue(true, completionListenerFor(subscriber));
                    }
                });
            }
        };
    }

    @NonNull
    private Func1<Throwable, ChannelWriteResult> errorConertedToWriteResult() {
        return new Func1<Throwable, ChannelWriteResult>() {
            @Override
            public ChannelWriteResult call(Throwable throwable) {
                return new ChannelWriteResult(throwable);
            }
        };
    }

    @NonNull
    private DatabaseReference.CompletionListener completionListenerFor(final Subscriber<? super ChannelWriteResult> subscriber) {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    subscriber.onNext(new ChannelWriteResult());
                } else {
                    subscriber.onError(databaseError.toException());
                }
            }
        };
    }

    private Observable<List<String>> privateChannelsFor(final User user) {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(final Subscriber<? super List<String>> subscriber) {
                DatabaseReference privateChannelsOfUserDb = FirebaseChannelService.this.privateChannelsDB.child(user.getId());
                getChannelsFromDatabase(subscriber, privateChannelsOfUserDb);

            }
        });
    }

    private Observable<List<String>> publicChannels() {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(final Subscriber<? super List<String>> subscriber) {
                DatabaseReference publicChannelsDB = FirebaseChannelService.this.publicChannelsDB;
                getChannelsFromDatabase(subscriber, publicChannelsDB);
            }
        });
    }

    private void getChannelsFromDatabase(Subscriber<? super List<String>> subscriber, DatabaseReference databaseReference) {
        ValueEventListener eventListener = databaseReference.addValueEventListener(new DataSubscriptionListener(subscriber));
        subscriber.add(removeEventListenerFromDatabase(databaseReference, eventListener));
    }

    @NonNull
    private BooleanSubscription removeEventListenerFromDatabase(final DatabaseReference publicChannelsDB, final ValueEventListener eventListener) {
        return BooleanSubscription.create(new Action0() {
            @Override
            public void call() {
                publicChannelsDB.removeEventListener(eventListener);
            }
        });
    }

    @NonNull
    private Func2<List<String>, List<String>, List<String>> mergeChannelLists() {
        return new Func2<List<String>, List<String>, List<String>>() {
            @Override
            public List<String> call(List<String> privateChannels, List<String> publicChannels) {
                List<String> mergedChannels = new ArrayList<>(privateChannels);
                mergedChannels.addAll(publicChannels);
                return mergedChannels;
            }
        };
    }

    @NonNull
    private Func1<List<String>, Observable<List<Channel>>> addChannelInfo() {
        return new Func1<List<String>, Observable<List<Channel>>>() {
            @Override
            public Observable<List<Channel>> call(List<String> strings) {
                return Observable.from(strings)
                        .map(createChannelInstanceFromPath())
                        .toList()
                        .flatMap(convertToListOfChannels());
            }
        };
    }

    @NonNull
    private Func1<String, Observable<Channel>> createChannelInstanceFromPath() {
        return new Func1<String, Observable<Channel>>() {
            @Override
            public Observable<Channel> call(String channelPath) {
                return getChannelInfoFor(channelPath)
                        .map(createChannelInstanceWith(channelPath));
            }
        };
    }

    @NonNull
    private Observable<ChannelInfo> getChannelInfoFor(final String channelPath) {
        return Observable.create(new Observable.OnSubscribe<ChannelInfo>() {
            @Override
            public void call(final Subscriber<? super ChannelInfo> subscriber) {
                channelsDB.child(channelPath).addListenerForSingleValueEvent(new ChannelInfoCheckingEventListener(subscriber));
            }
        });
    }

    @NonNull
    private Func1<List<Observable<Channel>>, Observable<List<Channel>>> convertToListOfChannels() {
        return new Func1<List<Observable<Channel>>, Observable<List<Channel>>>() {
            @Override
            public Observable<List<Channel>> call(List<Observable<Channel>> observables) {
                return Observable.merge(observables).toList();
            }
        };
    }

    @NonNull
    private Func1<ChannelInfo, Channel> createChannelInstanceWith(final String channelPath) {
        return new Func1<ChannelInfo, Channel>() {
            @Override
            public Channel call(final ChannelInfo channelInfo) {
                return new Channel(channelPath, channelInfo);
            }
        };
    }

    @NonNull
    private Func1<List<Channel>, Observable<Channels>> convertToChannelsInstance() {
        return new Func1<List<Channel>, Observable<Channels>>() {
            @Override
            public Observable<Channels> call(final List<Channel> channels) {
                return Observable.create(new Observable.OnSubscribe<Channels>() {
                    @Override
                    public void call(Subscriber<? super Channels> subscriber) {
                        subscriber.onNext(new Channels(channels));
                    }
                });
            }
        };
    }

    private ChannelInfo toChannelInfo(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(ChannelInfo.class);
    }

    private List<String> toChannels(DataSnapshot dataSnapshot) {
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

    private class DataSubscriptionListener implements ValueEventListener {
        private final Subscriber<? super List<String>> subscriber;

        private DataSubscriptionListener(Subscriber<? super List<String>> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            subscriber.onNext(toChannels(dataSnapshot));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }

    }

    private class ChannelInfoCheckingEventListener implements ValueEventListener {
        private final Subscriber<? super ChannelInfo> subscriber;

        public ChannelInfoCheckingEventListener(Subscriber<? super ChannelInfo> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                subscriber.onNext(toChannelInfo(dataSnapshot));
            }
            subscriber.onCompleted();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }
    }
}
