package com.novoda.bonfire.channel.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;

public class FirebaseChannelService implements ChannelService {

    private final DatabaseReference channelsDB;

    public FirebaseChannelService(FirebaseApp firebaseApp) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
        channelsDB = database.getReference("channels");
    }


    @Override
    public Observable<Channels> getChannels() {
        return Observable.create(new Observable.OnSubscribe<Channels>() {
            @Override
            public void call(final Subscriber<? super Channels> subscriber) {
                final ValueEventListener eventListener = channelsDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Channel> channels = toChannels(dataSnapshot);
                        subscriber.onNext(new Channels(channels));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
                    }

                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        channelsDB.removeEventListener(eventListener);
                    }
                }));
            }
        });
    }

    private List<Channel> toChannels(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        List<Channel> channels = new ArrayList<>();
        for (DataSnapshot child : children) {
            String channelName = child.getKey();
            channels.add(new Channel(channelName));
        }
        return channels;
    }
}
