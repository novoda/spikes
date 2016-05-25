package com.novoda.bonfire.channel.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.service.database.ChannelsDatabase;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;
import rx.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FirebaseChannelServiceTest {

    private static final String FIRST_PUBLIC_CHANNEL = "first public channel";
    private static final String FIRST_PRIVATE_CHANNEL = "first private channel";
    private static final String USER_ID = "test user id";

    private final Channel publicChannel = new Channel(FIRST_PUBLIC_CHANNEL, false);
    private final Channel privateChannel = new Channel(FIRST_PRIVATE_CHANNEL, true);
    private final User user = new User(USER_ID, "test username", "http://test.photo/url");

    @Test
    public void canGetCompleteListOfChannelsForAUser() {
        List<Channel> listOfPublicChannels = new ArrayList<>();
        listOfPublicChannels.add(publicChannel);

        List<Channel> listOfPrivateChannels = new ArrayList<>();
        listOfPrivateChannels.add(privateChannel);

        List<Channel> expectedList = new ArrayList<>();
        expectedList.addAll(listOfPublicChannels);
        expectedList.addAll(listOfPrivateChannels);

        FirebaseChannelService firebaseChannelService = new FirebaseChannelService(new FakeChannelsDatabase());

        Observable<Channels> channelsObservable = firebaseChannelService.getChannelsFor(user);
        TestObserver<Channels> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        channelsTestObserver.assertReceivedOnNext(Collections.singletonList(new Channels(expectedList)));
    }

    @Test
    public void canCreateAPublicChannel() {
        FakeChannelsDatabase channelsDatabase = new FakeChannelsDatabase();
        FirebaseChannelService firebaseChannelService = new FirebaseChannelService(channelsDatabase);

        Channel newChannel = new Channel("another public channel", false);
        Observable<DatabaseResult<Channel>> channelsObservable = firebaseChannelService.createPublicChannel(newChannel);
        TestObserver<DatabaseResult<Channel>> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        channelsTestObserver.assertReceivedOnNext(Collections.singletonList(new DatabaseResult<>(newChannel)));
        verify(channelsDatabase.getChannelsDB()).setValue(eq(true), any(DatabaseReference.CompletionListener.class));
    }

    private class FakeChannelsDatabase implements ChannelsDatabase {
        @Override
        public DatabaseReference getPublicChannelsDB() {
            DatabaseReference mockPublicChannelsDBReference = mock(DatabaseReference.class);

            DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);
            when(mockDataSnapshot.getKey()).thenReturn(FIRST_PUBLIC_CHANNEL);
            when(mockDataSnapshot.hasChildren()).thenReturn(true);
            when(mockDataSnapshot.getChildren()).thenReturn(Collections.singletonList(mockDataSnapshot));

            callValueEventListenerOn(mockPublicChannelsDBReference, mockDataSnapshot);
            return mockPublicChannelsDBReference;
        }

        @Override
        public DatabaseReference getPrivateChannelsDB() {
            DatabaseReference mockPrivateChannelsDBReference = mock(DatabaseReference.class);
            when(mockPrivateChannelsDBReference.child(USER_ID)).thenReturn(mockPrivateChannelsDBReference);

            DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);
            when(mockDataSnapshot.getKey()).thenReturn(FIRST_PRIVATE_CHANNEL);
            when(mockDataSnapshot.hasChildren()).thenReturn(true);
            when(mockDataSnapshot.getChildren()).thenReturn(Collections.singletonList(mockDataSnapshot));

            callValueEventListenerOn(mockPrivateChannelsDBReference, mockDataSnapshot);
            return mockPrivateChannelsDBReference;
        }

        @Override
        public DatabaseReference getChannelsDB() {
            DatabaseReference mockChannelsDBReference = mock(DatabaseReference.class);

            DatabaseReference mockChannelsDBReferenceForPublicChannel = mock(DatabaseReference.class);
            when(mockChannelsDBReference.child(FIRST_PUBLIC_CHANNEL)).thenReturn(mockChannelsDBReferenceForPublicChannel);

            DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);
            when(mockDataSnapshot.hasChildren()).thenReturn(true);
            when(mockDataSnapshot.getValue(Channel.class)).thenReturn(publicChannel);

            callListenerForSingleValueEventOn(mockChannelsDBReferenceForPublicChannel, mockDataSnapshot);

            DatabaseReference mockChannelsDBReferenceForPrivateChannel = mock(DatabaseReference.class);
            when(mockChannelsDBReference.child(FIRST_PRIVATE_CHANNEL)).thenReturn(mockChannelsDBReferenceForPrivateChannel);

            DataSnapshot anotherMockDataSnapshot = mock(DataSnapshot.class);
            when(anotherMockDataSnapshot.hasChildren()).thenReturn(true);
            when(anotherMockDataSnapshot.getValue(Channel.class)).thenReturn(privateChannel);

            callListenerForSingleValueEventOn(mockChannelsDBReferenceForPrivateChannel, anotherMockDataSnapshot);

            return mockChannelsDBReference;
        }

        @Override
        public DatabaseReference getOwnersDB() {
            return mock(DatabaseReference.class);
        }

        @Override
        public DatabaseReference getUsersDB() {
            return mock(DatabaseReference.class);
        }

        private void callValueEventListenerOn(DatabaseReference mockDBReference, final DataSnapshot mockDataSnapshot) {
            doAnswer(new DataSnapshotAnswer(mockDataSnapshot)).when(mockDBReference).addValueEventListener(any(ValueEventListener.class));
        }

        private void callListenerForSingleValueEventOn(DatabaseReference mockChannelsDBReferenceForPublicChannel, final DataSnapshot mockDataSnapshot) {
            doAnswer(new DataSnapshotAnswer(mockDataSnapshot)).when(mockChannelsDBReferenceForPublicChannel).addListenerForSingleValueEvent(any(ValueEventListener.class));
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
    }
}
