package com.novoda.bonfire.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import rx.Observable;
import rx.functions.Func1;

public class FirebaseObservableListeners {

    public <T> Observable<T> listenToValueEvents(DatabaseReference databaseReference, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToValueEventsOnSubscribe<>(databaseReference, marshaller));
    }

    public <T> Observable<T> listenToSingleValueEvents(DatabaseReference databaseReference, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToSingleValueOnSubscribe<>(databaseReference, marshaller));
    }

    public <T> Observable<T> removeValue(DatabaseReference databaseReference, T returnValue) {
        return Observable.create(new RemoveValueOnSubscribe<>(databaseReference, returnValue));
    }

    public <T, U> Observable<U> setValue(T value, DatabaseReference databaseReference, U returnValue) {
        return Observable.create(new SetValueOnSubscribe<>(value, databaseReference, returnValue));
    }

}
