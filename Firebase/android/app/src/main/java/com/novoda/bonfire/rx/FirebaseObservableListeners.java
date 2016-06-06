package com.novoda.bonfire.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import rx.Observable;
import rx.functions.Func1;

public final class FirebaseObservableListeners {

    private FirebaseObservableListeners() {
        throw new IllegalStateException("Non instantiable class");
    }

    public static <T> Observable<T> listenToValueEvents(Query query, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToValueEventsOnSubscribe<T>(query, marshaller));
    }

    public static <T> Observable<T> listenToSingleValueEvents(Query query, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToSingleValueOnSubscribe<T>(query, marshaller));
    }

    public static <T> Observable<T> removeValue(DatabaseReference databaseReference, T returnValue) {
        return Observable.create(new RemoveValueOnSubscribe<T>(databaseReference, returnValue));
    }

    public static <T, U> Observable<U> setValue(T value, DatabaseReference databaseReference, U returnValue) {
        return Observable.create(new SetValueOnSubscribe<>(value, databaseReference, returnValue));
    }

}
