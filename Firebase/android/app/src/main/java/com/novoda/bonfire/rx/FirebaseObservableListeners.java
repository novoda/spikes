package com.novoda.bonfire.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import rx.Observable;
import rx.functions.Func1;

public final class FirebaseObservableListeners {

    private FirebaseObservableListeners() {
        throw new IllegalStateException("Non instantiable class");
    }

    public static <T> Observable<T> listenToValueEvents(DatabaseReference databaseReference, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToValuEventsOnSubscribe<T>(databaseReference, marshaller));
    }

    public static <T> Observable<T> listenToSingleValueEvents(DatabaseReference databaseReference, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToSingleValueOnSubscribe<T>(databaseReference, marshaller));
    }

    public static <T> Observable<T> removeValue(DatabaseReference databaseReference, T returnValue) {
        return Observable.create(new RemoveValueOnSubscribe<T>(databaseReference, returnValue));
    }

    public static <T, U> Observable<U> setValue(T value, DatabaseReference databaseReference, U returnValue) {
        return Observable.create(new SetValueOnSubscribe<>(value, databaseReference, returnValue));
    }

}
