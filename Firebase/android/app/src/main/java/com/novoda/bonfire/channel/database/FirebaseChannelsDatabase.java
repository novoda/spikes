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

import static com.novoda.bonfire.channel.database.ChannelConverter.fromFirebaseChannel;
import static com.novoda.bonfire.channel.database.ChannelConverter.toFirebaseChannel;

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
        return firebaseObservableListeners.listenToSingleValueEvents(channelsDB.child(channelName), asChannel());
    }

    @Override
    public Observable<Channel> writeChannel(Channel newChannel) {
        return firebaseObservableListeners.setValue(toFirebaseChannel(newChannel), channelsDB.child(newChannel.getName()), newChannel);
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

    private static Func1<DataSnapshot, Channel> asChannel() {
        return new Func1<DataSnapshot, Channel>() {
            @Override
            public Channel call(DataSnapshot dataSnapshot) {
                return fromFirebaseChannel(dataSnapshot.getValue(FirebaseChannel.class));
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
