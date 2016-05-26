package com.novoda.bonfire.channel.database;

import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FirebaseChannelsDatabaseTest {

    @Mock
    DatabaseReference mockPublicChannelsDb;
    @Mock
    DatabaseReference mockPrivateChannelsDb;
    @Mock
    DatabaseReference mockChannelsDb;
    @Mock
    DatabaseReference mockOwnersDb;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObservePublicChannelIds() {

        FirebaseChannelsDatabase underTest = new FirebaseChannelsDatabase(mockPublicChannelsDb, mockPrivateChannelsDb, mockChannelsDb, mockOwnersDb);

    }
}
