package com.novoda.bonfire.channel.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.ChannelInfo;
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

    public FirebaseChannelService(FirebaseDatabase firebaseDatabase) {
        publicChannelsDB = firebaseDatabase.getReference("public-channels-index");
        privateChannelsDB = firebaseDatabase.getReference("private-channels-index");
        channelsDB = firebaseDatabase.getReference("channels");
    }

    @Override
    public Observable<Channels> getChannelsFor(User user) {
        return Observable.zip(privateChannelsFor(user), publicChannels(), mergeChannelLists())
                .flatMap(addChannelInfo())
                .flatMap(convertToChannelsInstance());
    }

    @Override
    public void createPublicChannel(Channel newChannel) {
        channelsDB.child(newChannel.getPath()).setValue(newChannel.getChannelInfo());
        publicChannelsDB.child(newChannel.getPath()).setValue(true);
    }

    private Observable<List<String>> privateChannelsFor(final User user) {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(final Subscriber<? super List<String>> subscriber) {
                final DatabaseReference privateChannelsOfUserDb = FirebaseChannelService.this.privateChannelsDB.child(user.getId());
                final ValueEventListener eventListener = privateChannelsOfUserDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(toChannels(dataSnapshot));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
                    }

                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        privateChannelsOfUserDb.removeEventListener(eventListener);
                    }
                }));

            }
        });
    }

    private Observable<List<String>> publicChannels() {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(final Subscriber<? super List<String>> subscriber) {
                final ValueEventListener eventListener = FirebaseChannelService.this.publicChannelsDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(toChannels(dataSnapshot));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
                    }

                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        FirebaseChannelService.this.publicChannelsDB.removeEventListener(eventListener);
                    }
                }));
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
            public Observable<Channel> call(final String channelPath) {
                return Observable.create(new Observable.OnSubscribe<ChannelInfo>() {
                    @Override
                    public void call(final Subscriber<? super ChannelInfo> subscriber) {
                        channelsDB.child(channelPath).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        });
                    }
                }).map(createChannelInstanceWith(channelPath));
            }
        };
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
}
