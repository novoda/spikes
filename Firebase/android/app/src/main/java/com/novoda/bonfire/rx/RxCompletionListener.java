package com.novoda.bonfire.rx;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import rx.Subscriber;

class RxCompletionListener<T> implements DatabaseReference.CompletionListener {

    private final Subscriber<? super T> subscriber;
    private final T successValue;

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
