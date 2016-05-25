package com.novoda.bonfire.channel.service.database;

import com.google.firebase.database.DatabaseReference;

public interface ChannelsDatabase {
    DatabaseReference getPublicChannelsDB();

    DatabaseReference getPrivateChannelsDB();

    DatabaseReference getChannelsDB();

    DatabaseReference getOwnersDB();

    DatabaseReference getUsersDB();

}
