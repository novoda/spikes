package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channel.Access;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.database.ChannelsDatabase;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.database.UserDatabase;

import java.util.ArrayList;
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
import rx.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PersistedChannelServiceTest {

    private static final String FIRST_PUBLIC_CHANNEL = "first public channel";
    private static final String FIRST_PRIVATE_CHANNEL = "first private channel";
    private static final String USER_ID = "test user id";
    private static final String ANOTHER_USER_ID = "another user id";

    private final Channel publicChannel = new Channel(FIRST_PUBLIC_CHANNEL, Access.PUBLIC);
    private final Channel privateChannel = new Channel(FIRST_PRIVATE_CHANNEL, Access.PRIVATE);
    private final User user = new User(USER_ID, "test username", "http://test.photo/url");
    private final User anotherUser = new User(ANOTHER_USER_ID, "another username", "http://another.url");

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

        doAnswer(new ChannelAsObservableAnswer(0)).when(mockChannelsDatabase).writeChannel(any(Channel.class));

        doAnswer(new ChannelAsObservableAnswer(0)).when(mockChannelsDatabase).writeChannelToPublicChannelIndex(any(Channel.class));

        doAnswer(new ChannelAsObservableAnswer(1)).when(mockChannelsDatabase).addOwnerToPrivateChannel(any(User.class), any(Channel.class));

        doAnswer(new ChannelAsObservableAnswer(1)).when(mockChannelsDatabase).addChannelToUserPrivateChannelIndex(any(User.class), any(Channel.class));

        doAnswer(new ChannelAsObservableAnswer(1)).when(mockChannelsDatabase).removeOwnerFromPrivateChannel(any(User.class), any(Channel.class));

        when(mockChannelsDatabase.observeOwnerIdsFor(privateChannel)).thenReturn(Observable.just(Arrays.asList(USER_ID, ANOTHER_USER_ID)));

        doAnswer(new Answer<Observable<User>>() {
            @Override
            public Observable<User> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(user);
            }
        }).when(mockUserDatabase).readUserFrom(USER_ID);

        doAnswer(new Answer<Observable<User>>() {
            @Override
            public Observable<User> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(anotherUser);
            }
        }).when(mockUserDatabase).readUserFrom(ANOTHER_USER_ID);
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
    public void canCreateAPublicChannel() {
        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Channel newChannel = new Channel("another public channel", Access.PUBLIC);
        Observable<DatabaseResult<Channel>> channelsObservable = persistedChannelService.createPublicChannel(newChannel);
        TestObserver<DatabaseResult<Channel>> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        verify(mockChannelsDatabase).writeChannel(newChannel);
        verify(mockChannelsDatabase).writeChannelToPublicChannelIndex(newChannel);
    }

    @Test
    public void canCreateAPrivateChannel() {
        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Channel newChannel = new Channel("another private channel", Access.PUBLIC);
        Observable<DatabaseResult<Channel>> channelsObservable = persistedChannelService.createPrivateChannel(newChannel, user);
        TestObserver<DatabaseResult<Channel>> channelsTestObserver = new TestObserver<>();
        channelsObservable.subscribe(channelsTestObserver);

        verify(mockChannelsDatabase).addOwnerToPrivateChannel(user, newChannel);
        verify(mockChannelsDatabase).addChannelToUserPrivateChannelIndex(user, newChannel);
        verify(mockChannelsDatabase).writeChannel(newChannel);
    }

    @Test
    public void canAddOwnerToPrivateChannel() {
        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Observable<DatabaseResult<User>> userObservable = persistedChannelService.addOwnerToPrivateChannel(privateChannel, user);
        TestObserver<DatabaseResult<User>> testObserver = new TestObserver<>();
        userObservable.subscribe(testObserver);

        verify(mockChannelsDatabase).addOwnerToPrivateChannel(user, privateChannel);
        verify(mockChannelsDatabase).addChannelToUserPrivateChannelIndex(user, privateChannel);
    }

    @Test
    public void canRemoveOwnerFromPrivateChannel() {
        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Observable<DatabaseResult<User>> userObservable = persistedChannelService.removeOwnerFromPrivateChannel(privateChannel, user);
        TestObserver<DatabaseResult<User>> testObserver = new TestObserver<>();
        userObservable.subscribe(testObserver);

        verify(mockChannelsDatabase).removeOwnerFromPrivateChannel(user, privateChannel);
        verify(mockChannelsDatabase).removeChannelFromUserPrivateChannelIndex(user, privateChannel);
    }

    @Test
    public void canGetOwnersOfSpecificChannel() {
        Users expectedUsersList = buildExpectedUsers();

        PersistedChannelService persistedChannelService = buildPersistedChannelService();

        Observable<DatabaseResult<Users>> channelsObservable = persistedChannelService.getOwnersOfChannel(privateChannel);
        TestObserver<DatabaseResult<Users>> usersTestObserver = new TestObserver<>();
        channelsObservable.subscribe(usersTestObserver);

        usersTestObserver.assertReceivedOnNext(Collections.singletonList(new DatabaseResult<>(expectedUsersList)));
    }

    private Users buildExpectedUsers() {
        return new Users(Arrays.asList(user, anotherUser));
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

    private static class ChannelAsObservableAnswer implements Answer<Observable<Channel>> {

        private final int channelArgPosition;

        public ChannelAsObservableAnswer(int channelArgPositionInMethodCall) {
            this.channelArgPosition = channelArgPositionInMethodCall;
        }

        @Override
        public Observable<Channel> answer(InvocationOnMock invocation) throws Throwable {
            Channel channel = (Channel) invocation.getArguments()[channelArgPosition];
            return Observable.just(channel);
        }
    }
}
