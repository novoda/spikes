package com.novoda.bonfire.user.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.rx.FirebaseObservableListeners;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static com.novoda.bonfire.helpers.FirebaseTestHelpers.*;

public class FirebaseUserDatabaseTest {
    private static final String USER_ID = "test user id";
    private static final String ANOTHER_USER_ID = "another user id";

    private final User user = new User(USER_ID, "test username", "http://test.photo/url");
    private final User anotherUser = new User(ANOTHER_USER_ID, "another username", "http://another.url");

    private final Users users = new Users(Arrays.asList(user, anotherUser));

    @Mock
    FirebaseDatabase mockFirebaseDatabase;
    @Mock
    DatabaseReference mockUsersDatabase;
    @Mock
    FirebaseObservableListeners mockListeners;

    FirebaseUserDatabase firebaseUserDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        setupDatabaseStubsFor("users", mockUsersDatabase, mockFirebaseDatabase);

        setupValueEventListenerFor(mockListeners, mockUsersDatabase, users);

        setupSingleValueEventListenerFor(mockListeners, mockUsersDatabase, user);

        firebaseUserDatabase = new FirebaseUserDatabase(mockFirebaseDatabase, mockListeners);
    }

    @Test
    public void canObserveUsers() {
        Observable<Users> usersObservable = firebaseUserDatabase.observeUsers();
        assertValueReceivedOnNext(usersObservable, users);
    }

    @Test
    public void canRetrieveUserObjectFromId() {
        Observable<User> userObservable = firebaseUserDatabase.readUserFrom(USER_ID);
        assertValueReceivedOnNext(userObservable, user);
    }

    @Test
    public void canSetNewUserValue() {

    }
}
