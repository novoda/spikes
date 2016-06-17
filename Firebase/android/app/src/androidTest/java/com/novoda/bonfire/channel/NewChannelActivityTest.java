package com.novoda.bonfire.channel;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.bonfire.R;
import com.novoda.bonfire.TestDependencies;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.user.data.model.User;

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
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewChannelActivityTest {

    private static final String VALID_CHANNEL_NAME = "\uD83C\uDDEB\uD83C\uDDF7"; // French flag
    private static final User AUTHENTICATED_USER = new User("user_id", "Test User", "http://invalid.url");

    @Rule
    public ActivityTestRule<NewChannelActivity> activity = new ActivityTestRule<>(NewChannelActivity.class, false, false);
    private final Answer<Observable<DatabaseResult<Channel>>> observableWithCreatedChannel = new Answer<Observable<DatabaseResult<Channel>>>() {
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

        when(loginService.getAuthentication()).thenReturn(Observable.just(new Authentication(AUTHENTICATED_USER)));
        when(chatService.getChat(any(Channel.class))).thenReturn(Observable.just(new DatabaseResult<>(new Chat(new ArrayList<Message>()))));
        when(channelService.createPublicChannel(any(Channel.class))).thenAnswer(observableWithCreatedChannel);
        when(channelService.createPrivateChannel(any(Channel.class), any(User.class))).thenAnswer(observableWithCreatedChannel);

        TestDependencies.init()
                .withLoginService(loginService)
                .withChatService(chatService)
                .withChannelService(channelService);

        activity.launchActivity(new Intent());
    }

    @Test
    public void channelWithEmptyNameCannotBeCreated() throws Exception {
        onView(withText(activity.getActivity().getString(R.string.create))).check(matches(not(isEnabled())));
    }

    @Test
    public void canCreatePublicChannel() throws Exception {
        onView(withId(R.id.new_channel_name)).perform(replaceText(VALID_CHANNEL_NAME), closeSoftKeyboard());
        onView(withText(activity.getActivity().getString(R.string.create))).perform(click());

        ArgumentCaptor<Channel> channelArgumentCaptor = ArgumentCaptor.forClass(Channel.class);
        verify(channelService).createPublicChannel(channelArgumentCaptor.capture());
        assertThat(channelArgumentCaptor.getValue().isPrivate(), equalTo(false));
        assertThat(channelArgumentCaptor.getValue().getName(), equalToIgnoringCase(VALID_CHANNEL_NAME));
    }

    @Test
    public void canCreatePrivateChannel() throws Exception {
        onView(withId(R.id.private_channel_switch)).perform(click());
        onView(withId(R.id.new_channel_name)).perform(replaceText(VALID_CHANNEL_NAME), closeSoftKeyboard());
        onView(withText(activity.getActivity().getString(R.string.create))).perform(click());

        ArgumentCaptor<Channel> channelArgumentCaptor = ArgumentCaptor.forClass(Channel.class);
        verify(channelService).createPrivateChannel(channelArgumentCaptor.capture(), any(User.class));
        assertThat(channelArgumentCaptor.getValue().isPrivate(), equalTo(true));
        assertThat(channelArgumentCaptor.getValue().getName(), equalToIgnoringCase(VALID_CHANNEL_NAME));
    }

    @Test
    public void channelOpensAfterBeingCreated() throws Exception {
        onView(withId(R.id.new_channel_name)).perform(replaceText(VALID_CHANNEL_NAME), closeSoftKeyboard());
        onView(withText(activity.getActivity().getString(R.string.create))).perform(click());

        onView(allOf(withText(equalToIgnoringCase(VALID_CHANNEL_NAME)), isDescendantOfA(withId(R.id.toolbar)))).check(matches(isDisplayed()));
    }

    @Test
    public void whenChannelNameIsTextThenAnErrorIsShown() throws Exception {
        onView(withId(R.id.new_channel_name)).perform(typeText("name"), closeSoftKeyboard());

        onView(withId(R.id.new_channel_name)).check(matches(hasErrorText(activity.getActivity().getString(R.string.only_single_emoji_allowed))));
    }
}
