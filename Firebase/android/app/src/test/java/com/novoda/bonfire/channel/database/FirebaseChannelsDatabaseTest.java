package com.novoda.bonfire.channel.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.rx.FirebaseObservableListeners;
import com.novoda.bonfire.user.data.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class FirebaseChannelsDatabaseTest {

    private static final String TEST_CHANNEL_ID = "new channel";

    private final User user = new User("user id", "user", "http://photo");
    private final Channel newChannel = createPublicChannel(TEST_CHANNEL_ID);

    private final List<String> publicChannelIds = Arrays.asList("first channel id", "second channel id");

    @Mock
    DatabaseReference mockPublicChannelsDb;
    @Mock
    DatabaseReference mockPrivateChannelsDb;
    @Mock
    DatabaseReference mockChannelsDb;
    @Mock
    DatabaseReference mockOwnersDb;
    @Mock
    FirebaseDatabase mockFirebaseDatabase;

    @Mock
    FirebaseObservableListeners mockListeners;

    FirebaseChannelsDatabase firebaseChannelsDatabase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        setupDatabaseStubsFor("public-channels-index", mockPublicChannelsDb, mockFirebaseDatabase);

        setupDatabaseStubsFor("private-channels-index", mockPrivateChannelsDb, mockFirebaseDatabase);

        setupDatabaseStubsFor("channels", mockChannelsDb, mockFirebaseDatabase);

        setupDatabaseStubsFor("owners", mockOwnersDb, mockFirebaseDatabase);

        doAnswer(new Answer<Observable<List<String>>>() {
            @Override
            public Observable<List<String>> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(publicChannelIds);
            }
        }).when(mockListeners).listenToValueEvents(eq(mockPublicChannelsDb), any(typeOfGetKeys()));

        firebaseChannelsDatabase = new FirebaseChannelsDatabase(mockFirebaseDatabase, mockListeners);
    }

    private static void setupDatabaseStubsFor(String databaseName, DatabaseReference databaseReference, FirebaseDatabase firebaseDatabase) {
        when(firebaseDatabase.getReference(databaseName)).thenReturn(databaseReference);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
    }

    @Test
    public void publicChannelsIdsAreObservedFromPublicChannelDatabase() {
        Observable<List<String>> listObservable = firebaseChannelsDatabase.observePublicChannelIds();
        TestObserver<List<String>> observer = new TestObserver<>();
        listObservable.subscribe(observer);
        observer.assertReceivedOnNext(Collections.singletonList(publicChannelIds));
    }

    @Test
    public void privateChannelsIdsForAUserAreObservedFromPrivateChannelDatabase() {
        firebaseChannelsDatabase.observePrivateChannelIdsFor(user);
        verify(mockListeners).listenToValueEvents(eq(mockPrivateChannelsDb), isA(typeOfGetKeys()));
    }

    @Test
    public void channelDetailsAreReadFromChannelsDatabase() {
        firebaseChannelsDatabase.readChannelFor("channel TEST_CHANNEL_ID");
        verify(mockListeners).listenToSingleValueEvents(eq(mockChannelsDb), isA(typeOfAs()));
    }

    @Test
    public void canSetNewChannelInChannelsDatabaseAndReturnIt() {
        firebaseChannelsDatabase.writeChannel(newChannel);
        verify(mockListeners).setValue(createPublicFirebaseChannel(TEST_CHANNEL_ID), mockChannelsDb, newChannel);
    }

    @Test
    public void canSetNewChannelInPublicChannelDatabaseAndReturnIt() {
        firebaseChannelsDatabase.writeChannelToPublicChannelIndex(newChannel);
        verify(mockListeners).setValue(true, mockPublicChannelsDb, newChannel);
    }

    @Test
    public void newChannelIsSetForUserInOwnersDatabase() {
        firebaseChannelsDatabase.addOwnerToPrivateChannel(user, newChannel);
        verify(mockListeners).setValue(true, mockOwnersDb, newChannel);
    }

    @Test
    public void channelCanBeRemovedFromOwnersDatabase() {
        firebaseChannelsDatabase.removeOwnerFromPrivateChannel(user, newChannel);
        verify(mockListeners).removeValue(mockOwnersDb, newChannel);
    }

    @Test
    public void canAddChannelToPrivateChannelDatabaseForUser() {
        firebaseChannelsDatabase.addChannelToUserPrivateChannelIndex(user, newChannel);
        verify(mockListeners).setValue(true, mockPrivateChannelsDb, newChannel);
    }

    @Test
    public void canRemoveChannelFromPrivateChannelDatabaseForUser() {
        firebaseChannelsDatabase.removeChannelFromUserPrivateChannelIndex(user, newChannel);
        verify(mockListeners).removeValue(mockPrivateChannelsDb, newChannel);
    }

    @Test
    public void canGetOwnerIdsForAChannelFromOwnersDatabase() {
        firebaseChannelsDatabase.observeOwnerIdsFor(newChannel);
        verify(mockListeners).listenToValueEvents(eq(mockOwnersDb), isA(typeOfGetKeys()));
    }

    private Class<Func1<DataSnapshot, List<String>>> typeOfGetKeys() {
        return (Class<Func1<DataSnapshot, List<String>>>) (Class) Func1.class;
    }

    private <T> Class<Func1<DataSnapshot, T>> typeOfAs() {
        return (Class<Func1<DataSnapshot, T>>) (Class) Func1.class;
    }

    @NonNull
    private static Channel createPublicChannel(String name) {
        return new Channel(name, Channel.Access.PUBLIC);
    }

    @NonNull
    private static FirebaseChannel createPublicFirebaseChannel(String channelId) {
        return new FirebaseChannel(channelId, "public");
    }
}
