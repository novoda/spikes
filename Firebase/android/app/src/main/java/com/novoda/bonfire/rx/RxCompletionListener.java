package com.novoda.bonfire.rx;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import rx.Observable;
import rx.Subscriber;

public class RxCompletionListener<T> implements DatabaseReference.CompletionListener {

    private final Subscriber<? super T> subscriber;
    private final T successValue;

    public static <T, U> Observable<U> setValue(final T value, final DatabaseReference databaseReference, final U returnValue) {
        return Observable.create(new Observable.OnSubscribe<U>() {
            @Override
            public void call(Subscriber<? super U> subscriber) {
                databaseReference.setValue(value, new RxCompletionListener<>(subscriber, returnValue));
            }
        });
    }

    public static <T> Observable<T> removeValue(final DatabaseReference databaseReference, final T returnValue) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                databaseReference.removeValue(new RxCompletionListener<>(subscriber, returnValue));
            }
        });
    }

    public RxCompletionListener(Subscriber<? super T> subscriber, T successValue) {
        this.subscriber = subscriber;
        this.successValue = successValue;
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError == null) {
            subscriber.onNext(successValue);
            subscriber.onCompleted();
        } else {
            subscriber.onError(databaseError.toException());
        }
    }

}
