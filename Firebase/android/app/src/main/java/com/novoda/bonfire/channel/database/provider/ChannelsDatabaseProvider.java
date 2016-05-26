package com.novoda.bonfire.channel.database.provider;

import com.google.firebase.database.DatabaseReference;

public interface ChannelsDatabaseProvider {

    DatabaseReference getPublicChannelsDB();

    DatabaseReference getPrivateChannelsDB();

    DatabaseReference getChannelsDB();

    DatabaseReference getOwnersDB();

}
