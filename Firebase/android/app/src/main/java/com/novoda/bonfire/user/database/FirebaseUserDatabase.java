package com.novoda.bonfire.user.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.rx.OnSubscribeDatabaseListener;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.novoda.bonfire.rx.RxSingleValueListener.listenToSingleValueEvents;

public class FirebaseUserDatabase implements UserDatabase {

    private final DatabaseReference usersDB;

    public FirebaseUserDatabase(FirebaseDatabase firebaseDatabase) {
        usersDB = firebaseDatabase.getReference("users");
    }

    @Override
    public Observable<Users> observeUsers() {
        return Observable.create(new OnSubscribeDatabaseListener<>(usersDB, toUsers()));
    }

    @Override
    public Observable<User> readUserFrom(String userId) {
        return listenToSingleValueEvents(usersDB.child(userId), as(User.class));
    }

    @Override
    public void writeCurrentUser(User user) {
        usersDB.child(user.getId()).setValue(user); //TODO handle errors
    }

    private Func1<DataSnapshot, Users> toUsers() {
        return new Func1<DataSnapshot, Users>() {
            @Override
            public Users call(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : children) {
                    User message = child.getValue(User.class);
                    users.add(message);
                }
                return new Users(users);
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
