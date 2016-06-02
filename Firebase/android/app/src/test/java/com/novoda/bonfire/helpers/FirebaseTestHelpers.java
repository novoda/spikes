package com.novoda.bonfire.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.rx.FirebaseObservableListeners;

import rx.Observable;
import rx.functions.Func1;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class FirebaseTestHelpers {
    public static void setupDatabaseStubsFor(String databaseName, DatabaseReference databaseReference, FirebaseDatabase firebaseDatabase) {
        when(firebaseDatabase.getReference(databaseName)).thenReturn(databaseReference);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
    }

    public static <T> Class<Func1<DataSnapshot, T>> marshallerType() {
        return (Class<Func1<DataSnapshot, T>>) (Class) Func1.class;
    }

    public static <T> void setupValueEventListenerFor(
            FirebaseObservableListeners listeners,
            DatabaseReference databaseReference,
            T returnValue) {
        when(listeners.listenToValueEvents(eq(databaseReference), any(FirebaseTestHelpers.<T>marshallerType()))).thenReturn(Observable.just(returnValue));
    }
}
