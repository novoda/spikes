package com.novoda.bonfire.channel.database.provider;

import com.google.firebase.database.FirebaseDatabase;

public class ChannelsDbProviderFactory {
    public static ChannelsDatabaseProvider getChannelsDbProvider(FirebaseDatabase database) {
        return new FirebaseChannelsDbProvider(database);
    }
}
