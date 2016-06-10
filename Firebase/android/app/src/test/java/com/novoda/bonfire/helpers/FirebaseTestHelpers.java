package com.novoda.bonfire.helpers;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.rx.FirebaseObservableListeners;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class FirebaseTestHelpers {
    public static void setupDatabaseStubsFor(String databaseName, DatabaseReference databaseReference, FirebaseDatabase firebaseDatabase) {
        when(firebaseDatabase.getReference(databaseName)).thenReturn(databaseReference);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(databaseReference.push()).thenReturn(databaseReference);
        when(databaseReference.limitToLast(anyInt())).thenReturn(databaseReference);
    }

    public static <T> void setupValueEventListenerFor(
            FirebaseObservableListeners listeners,
            DatabaseReference databaseReference,
            T returnValue) {
        setupValueEventListenerWithObservable(listeners, databaseReference, Observable.just(returnValue));
    }

    public static void setupErroringValueEventListenerFor(
            FirebaseObservableListeners listeners,
            DatabaseReference databaseReference,
            Throwable throwable) {
        setupValueEventListenerWithObservable(listeners, databaseReference, Observable.error(throwable));
    }

    private static <T> void setupValueEventListenerWithObservable(
            FirebaseObservableListeners listeners,
            DatabaseReference databaseReference,
            Observable<T> observable) {
        when(listeners.listenToValueEvents(
                eq(databaseReference),
                any(FirebaseTestHelpers.<T>marshallerType())
             )
        ).thenReturn(observable);
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

    public static <T> void assertValueReceivedOnNext(Observable<T> observable, T expectedValue) {
        TestSubscriber<T> testSubscriber = testSubscriberFor(observable);
        testSubscriber.assertValue(expectedValue);
    }

    public static <T> void assertThrowableReceivedOnError(Observable<T> observable, Throwable throwable) {
        TestSubscriber<T> testSubscriber = testSubscriberFor(observable);
        testSubscriber.assertError(throwable);
    }

    @NonNull
    private static <T> TestSubscriber<T> testSubscriberFor(Observable<T> observable) {
        TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        return testSubscriber;
    }

    private static <T> Class<Func1<DataSnapshot, T>> marshallerType() {
        return (Class<Func1<DataSnapshot, T>>) (Class) Func1.class;
    }
}
