package com.novoda.bonfire.channel.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.rx.OnSubscribeDatabaseListener;
import com.novoda.bonfire.rx.OnSubscribeRemoveValueListener;
import com.novoda.bonfire.rx.OnSubscribeSetValueListener;
import com.novoda.bonfire.rx.OnSubscribeSingleValueListener;
import com.novoda.bonfire.user.data.model.User;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class FirebaseChannelsDatabase implements ChannelsDatabase {

    private final DatabaseReference publicChannelsDB;
    private final DatabaseReference privateChannelsDB;
    private final DatabaseReference channelsDB;
    private final DatabaseReference ownersDB;

    public FirebaseChannelsDatabase(FirebaseDatabase firebaseDatabase) {
        this.publicChannelsDB = firebaseDatabase.getReference("public-channels-index");
        this.privateChannelsDB = firebaseDatabase.getReference("private-channels-index");
        this.channelsDB = firebaseDatabase.getReference("channels");
        this.ownersDB = firebaseDatabase.getReference("owners");
    }

    @Override
    public Observable<List<String>> observePublicChannelIds() {
        return Observable.create(new OnSubscribeDatabaseListener<>(publicChannelsDB, new DataSnapshotToStringListMarshaller()));
    }

    @Override
    public Observable<List<String>> observePrivateChannelIdsFor(User user) {
        return Observable.create(new OnSubscribeDatabaseListener<>(privateChannelsDB.child(user.getId()), new DataSnapshotToStringListMarshaller()));
    }

    @Override
    public Observable<Channel> readChannelFor(String channelName) {
        return Observable.create(new OnSubscribeSingleValueListener<>(channelsDB.child(channelName), as(Channel.class)));
    }

    @Override
    public Observable<Channel> writeChannel(Channel newChannel) {
        return Observable.create(new OnSubscribeSetValueListener<>(newChannel, channelsDB.child(newChannel.getName()), newChannel));
    }

    @Override
    public Observable<Channel> writeChannelToPublicChannelIndex(Channel newChannel) {
        return Observable.create(new OnSubscribeSetValueListener<>(true, publicChannelsDB.child(newChannel.getName()), newChannel));
    }

    @Override
    public Observable<Channel> addOwnerToPrivateChannel(User user, Channel channel) {
        return Observable.create(new OnSubscribeSetValueListener<>(true, ownersDB.child(channel.getName()).child(user.getId()), channel));
    }

    public Observable<Channel> removeOwnerFromPrivateChannel(User user, Channel channel) {
        return Observable.create(new OnSubscribeRemoveValueListener<>(ownersDB.child(channel.getName()).child(user.getId()), channel));
    }

    @Override
    public Observable<Channel> addChannelToUserPrivateChannelIndex(User user, Channel channel) {
        return Observable.create(new OnSubscribeSetValueListener<>(true, privateChannelsDB.child(user.getId()).child(channel.getName()), channel));
    }

    @Override
    public Observable<Channel> removeChannelFromUserPrivateChannelIndex(User user, Channel channel) {
        return Observable.create(new OnSubscribeRemoveValueListener<>(privateChannelsDB.child(user.getId()).child(channel.getName()), channel));
    }

    @Override
    public Observable<List<String>> observeOwnerIdsFor(Channel channel) {
        return Observable.create(new OnSubscribeDatabaseListener<>(ownersDB.child(channel.getName()), new DataSnapshotToStringListMarshaller()));
    }

    private static <T> Func1<DataSnapshot, T> as(final Class<T> tClass) {
        return new Func1<DataSnapshot, T>() {
            @Override
            public T call(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(tClass);
            }
        };
    }

}
