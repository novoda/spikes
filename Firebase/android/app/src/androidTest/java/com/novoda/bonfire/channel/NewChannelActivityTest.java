package com.novoda.bonfire.channel;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.service.UserService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewChannelActivityTest {

    @Rule
    public ActivityTestRule<NewChannelActivity> activity = new ActivityTestRule<>(NewChannelActivity.class, false, false);

    private final Answer<Observable<DatabaseResult<Channel>>> observableWithInsertedChannel = new Answer<Observable<DatabaseResult<Channel>>>() {
        @Override
        public Observable<DatabaseResult<Channel>> answer(InvocationOnMock invocation) throws Throwable {
            return Observable.just(new DatabaseResult<>((Channel) invocation.getArguments()[0]));
        }
    };
    private ChannelService channelService;

    @Before
    public void setUp() throws Exception {
        channelService = Mockito.mock(ChannelService.class);
        ChatService chatService = Mockito.mock(ChatService.class);
        LoginService loginService = Mockito.mock(LoginService.class);
        when(loginService.getAuthentication()).thenReturn(Observable.just(Mockito.mock(Authentication.class)));

        when(chatService.getChat(any(Channel.class)))
                .thenReturn(Observable.just(new DatabaseResult<>(new Chat(new ArrayList<Message>()))));
        when(channelService.createPublicChannel(any(Channel.class))).thenAnswer(observableWithInsertedChannel);
        when(channelService.createPrivateChannel(any(Channel.class), any(User.class))).thenAnswer(observableWithInsertedChannel);
        Dependencies.INSTANCE.setLoginService(loginService);
        Dependencies.INSTANCE.setUserService(Mockito.mock(UserService.class));
        Dependencies.INSTANCE.setChatService(chatService);
        Dependencies.INSTANCE.setChannelService(channelService);
        Dependencies.INSTANCE.setAnalytics(Mockito.mock(Analytics.class));

        activity.launchActivity(new Intent());
    }

    @Test
    public void channelWithEmptyNameCannotBeCreated() throws Exception {
        onView(withId(R.id.createButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void canCreatePublicChannel() throws Exception {
        String channelName = "public channel";
        ArgumentCaptor<Channel> channelArgumentCaptor = ArgumentCaptor.forClass(Channel.class);

        onView(withId(R.id.newChannelName)).perform(typeText(channelName), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());

        verify(channelService).createPublicChannel(channelArgumentCaptor.capture());
        assertThat(channelArgumentCaptor.getValue().isPrivate(), equalTo(false));
        assertThat(channelArgumentCaptor.getValue().getName(), equalTo(channelName));
    }

    @Test
    public void canCreatePrivateChannelWhen() throws Exception {
        String channelName = "private channel";
        ArgumentCaptor<Channel> channelArgumentCaptor = ArgumentCaptor.forClass(Channel.class);

        onView(withId(R.id.newChannelName)).perform(typeText(channelName), closeSoftKeyboard());
        onView(withId(R.id.privateChannel)).perform(click());
        onView(withId(R.id.createButton)).perform(click());

        verify(channelService).createPrivateChannel(channelArgumentCaptor.capture(), any(User.class));
        assertThat(channelArgumentCaptor.getValue().isPrivate(), equalTo(true));
        assertThat(channelArgumentCaptor.getValue().getName(), equalTo(channelName));
    }
}
