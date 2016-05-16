package com.novoda.bonfire.channel.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.login.data.model.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.subscriptions.BooleanSubscription;

public class FirebaseChannelService implements ChannelService {

    private final DatabaseReference publicChannelsDB;
    private final DatabaseReference privateChannelsDB;

    public FirebaseChannelService(FirebaseApp firebaseApp) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
        publicChannelsDB = database.getReference("channels-public-index");
        privateChannelsDB = database.getReference("channels-private-index");
    }

    @Override
    public Observable<Channels> getChannelsFor(User user) {
        return Observable.zip(
                privateChannelsFor(user),
                publicChannels(),
                new Func2<List<Channel>, List<Channel>, Channels>() {
                    @Override
                    public Channels call(List<Channel> channels, List<Channel> channels2) {
                        List<Channel> combinedChannels = new ArrayList<>(channels);
                        combinedChannels.addAll(channels2);
                        return new Channels(combinedChannels);
                    }
                }
        );
    }

    private Observable<List<Channel>> privateChannelsFor(final User user) {
        return Observable.create(new Observable.OnSubscribe<List<Channel>>() {
            @Override
            public void call(final Subscriber<? super List<Channel>> subscriber) {
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

    private Observable<List<Channel>> publicChannels() {
        return Observable.create(new Observable.OnSubscribe<List<Channel>>() {
            @Override
            public void call(final Subscriber<? super List<Channel>> subscriber) {
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

    private List<Channel> toChannels(DataSnapshot dataSnapshot) {
        List<Channel> channels = new ArrayList<>();
        if (dataSnapshot.hasChildren()) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            for (DataSnapshot child : children) {
                String channelName = child.getValue(String.class);
                channels.add(new Channel(channelName));
            }
        }
        return channels;
    }
}
