package com.novoda.bonfire.channel.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.rx.FirebaseObservableListeners;
import com.novoda.bonfire.user.data.model.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class FirebaseChannelsDatabase implements ChannelsDatabase {

    private final DatabaseReference publicChannelsDB;
    private final DatabaseReference privateChannelsDB;
    private final DatabaseReference channelsDB;
    private final DatabaseReference ownersDB;
    private final FirebaseObservableListeners firebaseObservableListeners;

    public FirebaseChannelsDatabase(FirebaseDatabase firebaseDatabase, FirebaseObservableListeners firebaseObservableListeners) {
        this.publicChannelsDB = firebaseDatabase.getReference("public-channels-index");
        this.privateChannelsDB = firebaseDatabase.getReference("private-channels-index");
        this.channelsDB = firebaseDatabase.getReference("channels");
        this.ownersDB = firebaseDatabase.getReference("owners");
        this.firebaseObservableListeners = firebaseObservableListeners;
    }

    @Override
    public Observable<List<String>> observePublicChannelIds() {
        return firebaseObservableListeners.listenToValueEvents(publicChannelsDB, getKeys());
    }

    @Override
    public Observable<List<String>> observePrivateChannelIdsFor(User user) {
        return firebaseObservableListeners.listenToValueEvents(privateChannelsDB.child(user.getId()), getKeys());
    }

    @Override
    public Observable<Channel> readChannelFor(String channelName) {
        return firebaseObservableListeners.listenToSingleValueEvents(channelsDB.child(channelName), as(Channel.class));
    }

    @Override
    public Observable<Channel> writeChannel(Channel newChannel) {
        return firebaseObservableListeners.setValue(newChannel, channelsDB.child(newChannel.getName()), newChannel);
    }

    @Override
    public Observable<Channel> writeChannelToPublicChannelIndex(Channel newChannel) {
        return firebaseObservableListeners.setValue(true, publicChannelsDB.child(newChannel.getName()), newChannel);
    }

    @Override
    public Observable<Channel> addOwnerToPrivateChannel(User user, Channel channel) {
        return firebaseObservableListeners.setValue(true, ownersDB.child(channel.getName()).child(user.getId()), channel);
    }

    @Override
    public Observable<Channel> removeOwnerFromPrivateChannel(User user, Channel channel) {
        return firebaseObservableListeners.removeValue(ownersDB.child(channel.getName()).child(user.getId()), channel);
    }

    @Override
    public Observable<Channel> addChannelToUserPrivateChannelIndex(User user, Channel channel) {
        return firebaseObservableListeners.setValue(true, privateChannelsDB.child(user.getId()).child(channel.getName()), channel);
    }

    @Override
    public Observable<Channel> removeChannelFromUserPrivateChannelIndex(User user, Channel channel) {
        return firebaseObservableListeners.removeValue(privateChannelsDB.child(user.getId()).child(channel.getName()), channel);
    }

    @Override
    public Observable<List<String>> observeOwnerIdsFor(Channel channel) {
        return firebaseObservableListeners.listenToValueEvents(ownersDB.child(channel.getName()), getKeys());
    }

    private static <T> Func1<DataSnapshot, T> as(final Class<T> tClass) {
        return new Func1<DataSnapshot, T>() {
            @Override
            public T call(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(tClass);
            }
        };
    }

    private static Func1<DataSnapshot, List<String>> getKeys() {
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

}
