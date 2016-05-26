package com.novoda.bonfire.channel.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.rx.ValueEventObservableCreator;
import com.novoda.bonfire.user.data.model.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.novoda.bonfire.rx.RxCompletionListener.removeValue;
import static com.novoda.bonfire.rx.RxCompletionListener.setValue;
import static com.novoda.bonfire.rx.RxSingleValueListener.listenToSingleValueEvents;

class FirebaseChannelsDatabase implements ChannelsDatabase {

    private final DatabaseReference publicChannelsDB;
    private final DatabaseReference privateChannelsDB;
    private final DatabaseReference channelsDB;
    private final DatabaseReference ownersDB;
    private final ValueEventObservableCreator valueEventObservableCreator;

    public FirebaseChannelsDatabase(
            DatabaseReference publicChannelsDB,
            DatabaseReference privateChannelsDB,
            DatabaseReference channelsDB,
            DatabaseReference ownersDB,
            ValueEventObservableCreator valueEventObservableCreator) {
        this.publicChannelsDB = publicChannelsDB;
        this.privateChannelsDB = privateChannelsDB;
        this.channelsDB = channelsDB;
        this.ownersDB = ownersDB;
        this.valueEventObservableCreator = valueEventObservableCreator;
    }

    @Override
    public Observable<List<String>> observePublicChannelIds() {
        return valueEventObservableCreator.listenToValueEvents(publicChannelsDB, getKeys());
    }

    @Override
    public Observable<List<String>> observePrivateChannelIdsFor(User user) {
        return valueEventObservableCreator.listenToValueEvents(privateChannelsDB.child(user.getId()), getKeys());
    }

    @Override
    public Observable<Channel> readChannelFor(String channelName) {
        return listenToSingleValueEvents(channelsDB.child(channelName), as(Channel.class));
    }

    @Override
    public Observable<Channel> writeChannel(Channel newChannel) {
        return setValue(newChannel, channelsDB.child(newChannel.getName()), newChannel);
    }

    @Override
    public Observable<Channel> writeChannelToPublicChannelIndex(Channel newChannel) {
        return setValue(true, publicChannelsDB.child(newChannel.getName()), newChannel);
    }

    @Override
    public Observable<Channel> addOwnerToPrivateChannel(User user, Channel channel) {
        return setValue(true, ownersDB.child(channel.getName()).child(user.getId()), channel);
    }

    public Observable<Channel> removeOwnerFromPrivateChannel(User user, Channel channel) {
        return removeValue(ownersDB.child(channel.getName()).child(user.getId()), channel);
    }

    @Override
    public Observable<Channel> addChannelToUserPrivateChannelIndex(User user, Channel channel) {
        return setValue(true, privateChannelsDB.child(user.getId()).child(channel.getName()), channel);
    }

    @Override
    public Observable<Channel> removeChannelFromUserPrivateChannelIndex(User user, Channel channel) {
        return removeValue(privateChannelsDB.child(user.getId()).child(channel.getName()), channel);
    }

    @Override
    public Observable<List<String>> observeOwnerIdsFor(Channel channel) {
        return valueEventObservableCreator.listenToValueEvents(ownersDB.child(channel.getName()), getKeys());
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
