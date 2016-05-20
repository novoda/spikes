package com.novoda.bonfire.channel.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.UserSearchResult;
import com.novoda.bonfire.channel.data.model.Users;
import com.novoda.bonfire.login.data.model.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.BooleanSubscription;

public class FirebaseUserService implements UserService {

    private final DatabaseReference usersDB;

    public FirebaseUserService(FirebaseDatabase firebaseDatabase) {
        usersDB = firebaseDatabase.getReference("users");
    }

    @Override
    public Observable<Users> getAllUsers() {
        return Observable.create(new Observable.OnSubscribe<Users>() {
            @Override
            public void call(final Subscriber<? super Users> subscriber) {
                final ValueEventListener eventListener = usersDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<User> users = toUsers(dataSnapshot);
                        subscriber.onNext(new Users(users));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
                    }

                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        usersDB.removeEventListener(eventListener);
                    }
                }));
            }
        });
    }

    @Override
    public Observable<UserSearchResult> getUserWithName(final String name) {
        return getAllUsers().flatMap(new Func1<Users, Observable<UserSearchResult>>() {
            @Override
            public Observable<UserSearchResult> call(Users users) {
                for (final User user : users.getUsers()) {
                    if (user.getName().toLowerCase().contains(name.trim().toLowerCase())) {
                        return Observable.create(new Observable.OnSubscribe<UserSearchResult>() {
                            @Override
                            public void call(Subscriber<? super UserSearchResult> subscriber) {
                                subscriber.onNext(new UserSearchResult(user));
                                subscriber.onCompleted();
                            }
                        });
                    }
                }
                return Observable.create(new Observable.OnSubscribe<UserSearchResult>() {
                    @Override
                    public void call(Subscriber<? super UserSearchResult> subscriber) {
                        subscriber.onNext(new UserSearchResult());
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    private List<User> toUsers(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        List<User> users = new ArrayList<>();
        for (DataSnapshot child : children) {
            User message = child.getValue(User.class);
            users.add(message);
        }
        return users;
    }
}
