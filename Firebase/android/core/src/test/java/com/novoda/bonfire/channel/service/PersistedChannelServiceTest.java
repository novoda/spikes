package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.database.ChannelsDatabase;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.database.UserDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.observers.TestObserver;

import static org.mockito.Mockito.when;

public class PersistedChannelServiceTest {

    private static final String FIRST_PUBLIC_CHANNEL = "first public channel";
    private static final String FIRST_PRIVATE_CHANNEL = "first private channel";
    private static final String USER_ID = "test user id";

    private final Channel publicChannel = new Channel(FIRST_PUBLIC_CHANNEL, false);
    private final Channel privateChannel = new Channel(FIRST_PRIVATE_CHANNEL, true);
    private final User user = new User(USER_ID, "test username", "http://test.photo/url");

    @Mock
    ChannelsDatabase mockChannelsDatabase;
    @Mock
    UserDatabase mockUserDatabase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mockChannelsDatabase.observePublicChannelIds()).thenReturn(Observable.just(Collections.singletonList(FIRST_PUBLIC_CHANNEL)));
        when(mockChannelsDatabase.observePrivateChannelIdsFor(user)).thenReturn(Observable.just(Collections.singletonList(FIRST_PRIVATE_CHANNEL)));
        when(mockChannelsDatabase.readChannelFor(FIRST_PUBLIC_CHANNEL)).thenReturn(Observable.just(publicChannel));
        when(mockChannelsDatabase.readChannelFor(FIRST_PRIVATE_CHANNEL)).thenReturn(Observable.just(privateChannel));
    }

    @Test
    public void canGetCompleteListOfChannelsForAUser() {
        List<Channel> expectedList = buildExpectedChannelsList();

        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Observable<Channels> channelsObservable = persistedChannelService.getChannelsFor(user);
        TestObserver<Channels> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        channelsTestObserver.assertReceivedOnNext(Collections.singletonList(new Channels(expectedList)));
    }

    @Test
    @Ignore
    public void canCreateAPublicChannel() {
        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Channel newChannel = new Channel("another public channel", false);
        Observable<DatabaseResult<Channel>> channelsObservable = persistedChannelService.createPublicChannel(newChannel);
        TestObserver<DatabaseResult<Channel>> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        channelsTestObserver.assertReceivedOnNext(Collections.singletonList(new DatabaseResult<>(newChannel)));
        //verify(channelsDatabase.getChannelsDB()).setValue(eq(true), any(DatabaseReference.CompletionListener.class));
    }

    private List<Channel> buildExpectedChannelsList() {
        List<Channel> listOfPublicChannels = new ArrayList<>();
        listOfPublicChannels.add(publicChannel);

        List<Channel> listOfPrivateChannels = new ArrayList<>();
        listOfPrivateChannels.add(privateChannel);

        List<Channel> expectedList = new ArrayList<>();
        expectedList.addAll(listOfPublicChannels);
        expectedList.addAll(listOfPrivateChannels);
        return expectedList;
    }

    private PersistedChannelService buildPersistedChannelService() {
        return new PersistedChannelService(mockChannelsDatabase, mockUserDatabase);
    }

    /* Commenting out everything in here so I can see if any of it is salvageable later
    @Test
    public void canCreateAPublicChannel() {
        FakeChannelsDatabaseProvider channelsDatabase = new FakeChannelsDatabaseProvider();
        PersistedChannelService persistedChannelService = new PersistedChannelService(channelsDatabase, userDatabase);

        Channel newChannel = new Channel("another public channel", false);
        Observable<DatabaseResult<Channel>> channelsObservable = persistedChannelService.createPublicChannel(newChannel);
        TestObserver<DatabaseResult<Channel>> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        channelsTestObserver.assertReceivedOnNext(Collections.singletonList(new DatabaseResult<>(newChannel)));
        //verify(channelsDatabase.getChannelsDB()).setValue(eq(true), any(DatabaseReference.CompletionListener.class));
    }

    private class FakeChannelsDatabaseProvider implements ChannelsDatabaseProvider {
        @Override
        public DatabaseReference getPublicChannelsDB() {
            DatabaseReference mockPublicChannelsDBReference = Mockito.mock(DatabaseReference.class);
            Mockito.when(mockPublicChannelsDBReference.child(Matchers.anyString())).thenReturn(mockPublicChannelsDBReference);

            setupSuccessfulCompletionListenerOn(mockPublicChannelsDBReference);

            DataSnapshot mockDataSnapshot = Mockito.mock(DataSnapshot.class);
            Mockito.when(mockDataSnapshot.getKey()).thenReturn(FIRST_PUBLIC_CHANNEL);
            Mockito.when(mockDataSnapshot.hasChildren()).thenReturn(true);
            Mockito.when(mockDataSnapshot.getChildren()).thenReturn(Collections.singletonList(mockDataSnapshot));

            callValueEventListenerOn(mockPublicChannelsDBReference, mockDataSnapshot);
            return mockPublicChannelsDBReference;
        }

        @Override
        public DatabaseReference getPrivateChannelsDB() {
            DatabaseReference mockPrivateChannelsDBReference = Mockito.mock(DatabaseReference.class);
            Mockito.when(mockPrivateChannelsDBReference.child(USER_ID)).thenReturn(mockPrivateChannelsDBReference);

            DataSnapshot mockDataSnapshot = Mockito.mock(DataSnapshot.class);
            Mockito.when(mockDataSnapshot.getKey()).thenReturn(FIRST_PRIVATE_CHANNEL);
            Mockito.when(mockDataSnapshot.hasChildren()).thenReturn(true);
            Mockito.when(mockDataSnapshot.getChildren()).thenReturn(Collections.singletonList(mockDataSnapshot));

            callValueEventListenerOn(mockPrivateChannelsDBReference, mockDataSnapshot);
            return mockPrivateChannelsDBReference;
        }

        @Override
        public DatabaseReference getChannelsDB() {
            DatabaseReference mockChannelsDBReference = Mockito.mock(DatabaseReference.class);

            final DatabaseReference mockPublicChannelsDBReference = Mockito.mock(DatabaseReference.class);

            final DatabaseReference mockPrivateChannelsDBReference = Mockito.mock(DatabaseReference.class);

            final DatabaseReference mockNewChannelDBReference = Mockito.mock(DatabaseReference.class);

            Mockito.doAnswer(new Answer<DatabaseReference>() {
                @Override
                public DatabaseReference answer(InvocationOnMock invocation) throws Throwable {
                    if (invocation.getArguments()[0].equals(FIRST_PUBLIC_CHANNEL)) {
                        return mockPublicChannelsDBReference;
                    }
                    if (invocation.getArguments()[0].equals(FIRST_PRIVATE_CHANNEL)) {
                        return mockPrivateChannelsDBReference;
                    }
                    return mockNewChannelDBReference;
                }
            }).when(mockChannelsDBReference).child(Matchers.anyString());

            DataSnapshot mockDataSnapshot = Mockito.mock(DataSnapshot.class);
            Mockito.when(mockDataSnapshot.hasChildren()).thenReturn(true);
            Mockito.when(mockDataSnapshot.getValue(Channel.class)).thenReturn(publicChannel);
            callListenerForSingleValueEventOn(mockPublicChannelsDBReference, mockDataSnapshot);

            DataSnapshot anotherMockDataSnapshot = Mockito.mock(DataSnapshot.class);
            Mockito.when(anotherMockDataSnapshot.hasChildren()).thenReturn(true);
            Mockito.when(anotherMockDataSnapshot.getValue(Channel.class)).thenReturn(privateChannel);
            callListenerForSingleValueEventOn(mockPrivateChannelsDBReference, anotherMockDataSnapshot);

            setupSuccessfulCompletionListenerOn(mockNewChannelDBReference);

            return mockChannelsDBReference;
        }

        @Override
        public DatabaseReference getOwnersDB() {
            return Mockito.mock(DatabaseReference.class);
        }

        @Override
        public DatabaseReference getUsersDB() {
            return Mockito.mock(DatabaseReference.class);
        }

        private void callValueEventListenerOn(DatabaseReference mockDBReference, final DataSnapshot mockDataSnapshot) {
            Mockito.doAnswer(new DataSnapshotAnswer(mockDataSnapshot)).when(mockDBReference).addValueEventListener(Matchers.any(ValueEventListener.class));
        }

        private void callListenerForSingleValueEventOn(DatabaseReference mockChannelsDBReferenceForPublicChannel, final DataSnapshot mockDataSnapshot) {
            Mockito.doAnswer(new DataSnapshotAnswer(mockDataSnapshot)).when(mockChannelsDBReferenceForPublicChannel).addListenerForSingleValueEvent(Matchers.any(ValueEventListener.class));
        }

        private void setupSuccessfulCompletionListenerOn(DatabaseReference mockPublicChannelsDBReference) {
            Mockito.doAnswer(new DatabaseReferenceCompletionListenerAnswer(mockPublicChannelsDBReference))
                    .when(mockPublicChannelsDBReference)
                    .setValue(Matchers.anyObject(), Matchers.any(DatabaseReference.CompletionListener.class));
        }

        private class DataSnapshotAnswer implements Answer<Void> {
            private final DataSnapshot mockDataSnapshot;

            public DataSnapshotAnswer(DataSnapshot mockDataSnapshot) {
                this.mockDataSnapshot = mockDataSnapshot;
            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                ValueEventListener argument = (ValueEventListener) arguments[0];

                argument.onDataChange(mockDataSnapshot);
                return null;
            }
        }

        private class DatabaseReferenceCompletionListenerAnswer implements Answer<Void> {
            private final DatabaseReference mockDBReference;

            public DatabaseReferenceCompletionListenerAnswer(DatabaseReference mockDBReference) {
                this.mockDBReference = mockDBReference;
            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                DatabaseReference.CompletionListener argument = (DatabaseReference.CompletionListener) arguments[1];

                argument.onComplete(null, mockDBReference);

                return null;
            }
        }
    }

    */
}
