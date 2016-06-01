package com.novoda.bonfire.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

class ListenToSingleValueOnSubscribe<T> implements Observable.OnSubscribe<T> {

    private final DatabaseReference databaseReference;
    private final Func1<DataSnapshot, T> marshaller;

    ListenToSingleValueOnSubscribe(DatabaseReference databaseReference, Func1<DataSnapshot, T> marshaller) {
        this.databaseReference = databaseReference;
        this.marshaller = marshaller;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        databaseReference.addListenerForSingleValueEvent(new RxSingleValueListener<>(subscriber, marshaller));
    }

    private static class RxSingleValueListener<T> implements ValueEventListener {

        private final Subscriber<? super T> subscriber;
        private final Func1<DataSnapshot, T> marshaller;

        public RxSingleValueListener(Subscriber<? super T> subscriber, Func1<DataSnapshot, T> marshaller) {
            this.subscriber = subscriber;
            this.marshaller = marshaller;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChildren() && !subscriber.isUnsubscribed()) {
                subscriber.onNext(marshaller.call(dataSnapshot));
            }
            subscriber.onCompleted();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }

    }
}
