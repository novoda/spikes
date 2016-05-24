package com.novoda.bonfire.user.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

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
                        subscriber.onCompleted();
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
    public Observable<DatabaseResult<Users>> getUsersForIds(List<String> userIds) {
        return Observable.from(userIds)
                .flatMap(getUserWithId())
                .toList()
                .flatMap(convertToResultWithUsersInstance())
                .onErrorReturn(convertErrorToResult());

    }

    @NonNull
    private Func1<String, Observable<User>> getUserWithId() {
        return new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String userId) {
                return Observable.create(new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(final Subscriber<? super User> subscriber) {
                        usersDB.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                subscriber.onNext(toUser(dataSnapshot));
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                subscriber.onError(databaseError.toException());
                            }
                        });
                    }
                });
            }
        };
    }

    @NonNull
    private Func1<List<User>, Observable<DatabaseResult<Users>>> convertToResultWithUsersInstance() {
        return new Func1<List<User>, Observable<DatabaseResult<Users>>>() {
            @Override
            public Observable<DatabaseResult<Users>> call(final List<User> users) {
                return Observable.create(new Observable.OnSubscribe<DatabaseResult<Users>>() {
                    @Override
                    public void call(Subscriber<? super DatabaseResult<Users>> subscriber) {
                        subscriber.onNext(new DatabaseResult<>(new Users(users)));
                        subscriber.onCompleted();
                    }
                });
            }
        };
    }

    @NonNull
    private Func1<Throwable, DatabaseResult<Users>> convertErrorToResult() {
        return new Func1<Throwable, DatabaseResult<Users>>() {
            @Override
            public DatabaseResult<Users> call(Throwable throwable) {
                return new DatabaseResult<>(throwable);
            }
        };
    }

    private User toUser(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(User.class);
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
