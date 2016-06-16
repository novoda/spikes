package com.novoda.bonfire.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ListenToValueEventsOnSubscribeTest {

    private static final String EXPECTED_KEY = "EXPECTED_KEY";

    @Test
    public void canGetKeyFromDataSnapshot() {

        final DataSnapshot expectedKeyData = mock(DataSnapshot.class);
        when(expectedKeyData.getKey()).thenReturn(EXPECTED_KEY);

        Query query = mock(Query.class);

        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
                valueEventListener.onDataChange(expectedKeyData);
                return valueEventListener;
            }
        }).when(query).addValueEventListener(any(ValueEventListener.class));

        Observable<String> observable = Observable.create(new ListenToValueEventsOnSubscribe<>(query, getKey()));

        TestObserver<String> testObserver = new TestObserver<>();
        observable.subscribe(testObserver);

        testObserver.assertReceivedOnNext(Collections.singletonList(EXPECTED_KEY));
    }

    private static Func1<DataSnapshot, String> getKey() {
        return new Func1<DataSnapshot, String>() {
            @Override
            public String call(DataSnapshot dataSnapshot) {
                return dataSnapshot.getKey();
            }
        };
    }

}
