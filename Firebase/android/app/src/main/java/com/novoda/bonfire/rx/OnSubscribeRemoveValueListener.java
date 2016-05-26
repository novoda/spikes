package com.novoda.bonfire.rx;

import com.google.firebase.database.DatabaseReference;

import rx.Observable;
import rx.Subscriber;

public class OnSubscribeRemoveValueListener<T> implements Observable.OnSubscribe<T> {

    private final DatabaseReference databaseReference;
    private final T returnValue;

    public OnSubscribeRemoveValueListener(DatabaseReference databaseReference, T returnValue) {
        this.databaseReference = databaseReference;
        this.returnValue = returnValue;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        databaseReference.removeValue(new RxCompletionListener<>(subscriber, returnValue));
    }
}
