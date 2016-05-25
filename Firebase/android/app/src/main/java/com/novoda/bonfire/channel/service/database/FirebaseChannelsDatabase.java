package com.novoda.bonfire.channel.service.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class FirebaseChannelsDatabase implements ChannelsDatabase {
    private final FirebaseDatabase firebaseDatabase;

    public FirebaseChannelsDatabase(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    @Override
    public DatabaseReference getPublicChannelsDB() {
        return firebaseDatabase.getReference("public-channels-index");
    }

    @Override
    public DatabaseReference getPrivateChannelsDB() {
        return firebaseDatabase.getReference("private-channels-index");
    }

    @Override
    public DatabaseReference getChannelsDB() {
        return firebaseDatabase.getReference("channels");
    }

    @Override
    public DatabaseReference getOwnersDB() {
        return firebaseDatabase.getReference("owners");
    }

    @Override
    public DatabaseReference getUsersDB() {
        return firebaseDatabase.getReference("users");
    }
}
