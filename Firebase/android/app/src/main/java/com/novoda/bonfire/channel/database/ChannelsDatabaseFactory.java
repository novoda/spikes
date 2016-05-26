package com.novoda.bonfire.channel.database;

import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.rx.ValueEventObservableCreator;

public final class ChannelsDatabaseFactory {

    private ChannelsDatabaseFactory() {
        // Don't want to instantiate this class
    }

    public static ChannelsDatabase buildChannelsDatabase(FirebaseDatabase firebaseDatabase) {
        return new FirebaseChannelsDatabase(
                firebaseDatabase.getReference("private-channels-index"),
                firebaseDatabase.getReference("private-channels-index"),
                firebaseDatabase.getReference("channels"),
                firebaseDatabase.getReference("owners"),
                new ValueEventObservableCreator()
        );
    }
}
