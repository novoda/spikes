package com.novoda.bonfire.channel.service.database;

import com.google.firebase.database.FirebaseDatabase;

public class ChannelsDatabaseFactory {
    public static ChannelsDatabase getChannelsDatabase(FirebaseDatabase database) {
        return new FirebaseChannelsDatabase(database);
    }
}
