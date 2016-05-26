package com.novoda.bonfire.rx;

import com.google.firebase.database.DatabaseReference;

import rx.Observable;
import rx.Subscriber;

public class OnSubscribeSetValueListener<T, U> implements Observable.OnSubscribe<U> {

    private final T value;
    private final DatabaseReference databaseReference;
    private final U returnValue;

    public OnSubscribeSetValueListener(T value, DatabaseReference databaseReference, U returnValue) {
        this.value = value;
        this.databaseReference = databaseReference;
        this.returnValue = returnValue;
    }

    @Override
    public void call(Subscriber<? super U> subscriber) {
        databaseReference.setValue(value, new RxCompletionListener<>(subscriber, returnValue));
    }
}
