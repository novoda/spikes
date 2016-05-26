package com.novoda.bonfire.channel.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.novoda.bonfire.rx.OnSubscribeDatabaseListener;
import com.novoda.bonfire.user.data.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    OnSubscribeDatabaseListener mockOnSubscribeDatabaseListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void publicChanneLListIsReturnedAsObservableFromPublicChannelsDb() {
        FirebaseChannelsDatabase firebaseChannelsDatabase = createFirebaseChannelsDatabase();

        firebaseChannelsDatabase.observePublicChannelIds();

//        verify(mockOnSubscribeDatabaseListener).listenToValueEvents(eq(mockPublicChannelsDb), any(DataSnapshotToStringListMarshaller.class));
    }

    @Test
    public void privateChannelListIsReturnedAsObservableFromPrivateChannelsDb() {
        User user = new User("user id", "Hal Novoda", "http://test.url");
        when(mockPrivateChannelsDb.child(user.getId())).thenReturn(mockPrivateChannelsDb);

        FirebaseChannelsDatabase firebaseChannelsDatabase = createFirebaseChannelsDatabase();

        firebaseChannelsDatabase.observePrivateChannelIdsFor(user);

//        verify(mockOnSubscribeDatabaseListener).listenToValueEvents(eq(mockPrivateChannelsDb), any(DataSnapshotToStringListMarshaller.class));
    }

    @NonNull
    private FirebaseChannelsDatabase createFirebaseChannelsDatabase() {
        return new FirebaseChannelsDatabase(mockPublicChannelsDb, mockPrivateChannelsDb, mockChannelsDb, mockOwnersDb);
    }
}
