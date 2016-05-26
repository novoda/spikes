package com.novoda.bonfire.channel.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.novoda.bonfire.rx.ValueEventObservableCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class FirebaseChannelsDatabaseTest {

    @Mock
    DatabaseReference mockPublicChannelsDb;
    @Mock
    DatabaseReference mockPrivateChannelsDb;
    @Mock
    DatabaseReference mockChannelsDb;
    @Mock
    DatabaseReference mockOwnersDb;

    @Mock
    ValueEventObservableCreator mockValueEventObservableCreator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObservePublicChannelIds() {
        List<String> expectedPublicChannelList = new ArrayList<>();
        expectedPublicChannelList.add("publicChannelIdOne");
        expectedPublicChannelList.add("public channel id two");

        when(mockValueEventObservableCreator.listenToValueEvents(eq(mockPublicChannelsDb), any(Func1.class))).thenReturn(Observable.just(expectedPublicChannelList));

        FirebaseChannelsDatabase firebaseChannelsDatabase = createFirebaseChannelsDatabase();

        TestObserver<List<String>> testObserver = new TestObserver<>();
        firebaseChannelsDatabase.observePublicChannelIds().subscribe(testObserver);

        testObserver.assertReceivedOnNext(Collections.singletonList(expectedPublicChannelList));
    }

    @NonNull
    private FirebaseChannelsDatabase createFirebaseChannelsDatabase() {
        return new FirebaseChannelsDatabase(mockPublicChannelsDb, mockPrivateChannelsDb, mockChannelsDb, mockOwnersDb, mockValueEventObservableCreator);
    }
}
