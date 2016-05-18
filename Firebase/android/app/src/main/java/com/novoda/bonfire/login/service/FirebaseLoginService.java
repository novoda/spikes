package com.novoda.bonfire.login.service;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.data.model.User;
import com.novoda.notils.logger.simple.Log;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class FirebaseLoginService implements LoginService {

    private final FirebaseAuth firebaseAuth;

    private final BehaviorRelay<Authentication> authRelay;
    private final DatabaseReference usersDB;

    public FirebaseLoginService(FirebaseDatabase firebaseDatabase, FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        usersDB = firebaseDatabase.getReference("users");
        authRelay = BehaviorRelay.create();
    }

    @Override
    public Observable<Authentication> getAuthentication() {
        return authRelay
                .startWith(initRelay());
    }

    private Observable<Authentication> initRelay() {
        return Observable.defer(new Func0<Observable<Authentication>>() {
            @Override
            public Observable<Authentication> call() {
                if (authRelay.hasValue() && authRelay.getValue().isSuccess()) {
                    return Observable.empty();
                } else {
                    return fetchUser();
                }
            }
        });
    }

    private Observable<Authentication> fetchUser() {
        return Observable.create(new Observable.OnSubscribe<Authentication>() {
            @Override
            public void call(Subscriber<? super Authentication> subscriber) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    subscriber.onNext(authenticationFrom(currentUser));
                }
                subscriber.onCompleted();
            }
        }).doOnNext(authRelay).ignoreElements();
    }

    private Authentication authenticationFrom(FirebaseUser currentUser) {
        return new Authentication(new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString()));
    }

    @Override
    public void loginWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            Authentication authentication = authenticationFrom(firebaseUser);
                            usersDB.child(firebaseUser.getUid()).setValue(authentication.getUser());
                            authRelay.call(authentication);
                        } else {
                            Throwable exception = task.getException();
                            Log.e(exception, "Failed to authenticate Firebase");
                            authRelay.call(new Authentication(exception));
                        }
                    }
                });
    }

}
