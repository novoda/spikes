package com.novoda.bonfire.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.rx.FirebaseObservableListeners;

import java.util.Collections;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestObserver;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class FirebaseTestHelpers {
    public static void setupDatabaseStubsFor(String databaseName, DatabaseReference databaseReference, FirebaseDatabase firebaseDatabase) {
        when(firebaseDatabase.getReference(databaseName)).thenReturn(databaseReference);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(databaseReference.push()).thenReturn(databaseReference);
    }

    public static <T> Class<Func1<DataSnapshot, T>> marshallerType() {
        return (Class<Func1<DataSnapshot, T>>) (Class) Func1.class;
    }

    public static <T> void setupValueEventListenerFor(
            FirebaseObservableListeners listeners,
            DatabaseReference databaseReference,
            T returnValue) {
        when(listeners.listenToValueEvents(
                eq(databaseReference),
                any(FirebaseTestHelpers.<T>marshallerType())
             )
        ).thenReturn(Observable.just(returnValue));
    }

    public static <T> void assertValueReceivedOnNext(Observable<T> observable, T expectedValue) {
        TestObserver<T> observer = new TestObserver<>();
        observable.subscribe(observer);
        observer.assertReceivedOnNext(Collections.singletonList(expectedValue));
    }

    public static <T> void setupSingleValueEventListenerFor(
            FirebaseObservableListeners listeners,
            DatabaseReference databaseReference,
            T returnValue) {
        when(listeners.listenToSingleValueEvents(
                eq(databaseReference),
                any(FirebaseTestHelpers.<T>marshallerType())
             )
        ).thenReturn(Observable.just(returnValue));
    }
}
