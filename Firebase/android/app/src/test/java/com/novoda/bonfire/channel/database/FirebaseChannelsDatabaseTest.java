package com.novoda.bonfire.channel.database;

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

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestObserver;

import static com.novoda.bonfire.helpers.FirebaseTestHelpers.setupDatabaseStubsFor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.when;

public class FirebaseChannelsDatabaseTest {

    private final User user = new User("user id", "user", "http://photo");
    private final Channel newChannel = new Channel("new channel", Channel.Access.PUBLIC);

    private final List<String> publicChannelIds = Arrays.asList("first public id", "second public id");
    private final List<String> privateChannelIds = Arrays.asList("first private id", "second private id");
    private final List<String> ownerIds = Arrays.asList("first owner id", "second owner id");

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

        when(mockListeners.listenToValueEvents(eq(mockPublicChannelsDb), any(typeOfGetKeys()))).thenReturn(Observable.just(publicChannelIds));

        when(mockListeners.listenToValueEvents(eq(mockPrivateChannelsDb), any(typeOfGetKeys()))).thenReturn(Observable.just(privateChannelIds));
        when(mockListeners.listenToValueEvents(eq(mockOwnersDb), any(typeOfGetKeys()))).thenReturn(Observable.just(ownerIds));

        when(mockListeners.listenToSingleValueEvents(eq(mockChannelsDb), any(typeOfAsChannel()))).thenReturn(Observable.just(newChannel));

        when(mockListeners.setValue(anyObject(), any(DatabaseReference.class), any(Channel.class))).thenReturn(Observable.just(newChannel));

        when(mockListeners.removeValue(any(DatabaseReference.class), any(Channel.class))).thenReturn(Observable.just(newChannel));

        firebaseChannelsDatabase = new FirebaseChannelsDatabase(mockFirebaseDatabase, mockListeners);
    }

    @Test
    public void publicChannelsIdsAreObservedFromPublicChannelDatabase() {
        Observable<List<String>> listObservable = firebaseChannelsDatabase.observePublicChannelIds();
        assertValueReceivedOnNext(listObservable, publicChannelIds);
    }

    @Test
    public void privateChannelsIdsForAUserAreObservedFromPrivateChannelDatabase() {
        Observable<List<String>> listObservable = firebaseChannelsDatabase.observePrivateChannelIdsFor(user);
        assertValueReceivedOnNext(listObservable, privateChannelIds);
    }

    @Test
    public void channelDetailsAreReadFromChannelsDatabase() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.readChannelFor("new channel");
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void canSetNewChannelInChannelsDatabaseAndReturnIt() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.writeChannel(newChannel);
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void canSetNewChannelInPublicChannelDatabaseAndReturnIt() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.writeChannelToPublicChannelIndex(newChannel);
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void newChannelIsSetForUserInOwnersDatabase() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.addOwnerToPrivateChannel(user, newChannel);
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void channelCanBeRemovedFromOwnersDatabase() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.removeOwnerFromPrivateChannel(user, newChannel);
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void canAddChannelToPrivateChannelDatabaseForUser() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.addChannelToUserPrivateChannelIndex(user, newChannel);
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void canRemoveChannelFromPrivateChannelDatabaseForUser() {
        Observable<Channel> channelObservable = firebaseChannelsDatabase.removeChannelFromUserPrivateChannelIndex(user, newChannel);
        assertValueReceivedOnNext(channelObservable, newChannel);
    }

    @Test
    public void canGetOwnerIdsForAChannelFromOwnersDatabase() {
        Observable<List<String>> listObservable = firebaseChannelsDatabase.observeOwnerIdsFor(newChannel);
        assertValueReceivedOnNext(listObservable, ownerIds);
    }

    private <T> void assertValueReceivedOnNext(Observable<T> observable, T expectedValue) {
        TestObserver<T> observer = new TestObserver<>();
        observable.subscribe(observer);
        observer.assertReceivedOnNext(Collections.singletonList(expectedValue));
    }

    private Class<Func1<DataSnapshot, List<String>>> typeOfGetKeys() {
        return (Class<Func1<DataSnapshot, List<String>>>) (Class) Func1.class;
    }

    private Class<Func1<DataSnapshot, Channel>> typeOfAsChannel() {
        return (Class<Func1<DataSnapshot, Channel>>) (Class) Func1.class;
    }
}
