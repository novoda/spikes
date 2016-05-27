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
import com.novoda.bonfire.user.service.UserService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewChannelActivityTest {

    @Rule
    public ActivityTestRule<NewChannelActivity> activity = new ActivityTestRule<>(NewChannelActivity.class, false, false);
    private ChannelService channelService;
    private ChatService chatService;

    @Before
    public void setUp() throws Exception {
        channelService = Mockito.mock(ChannelService.class);
        chatService = Mockito.mock(ChatService.class);
        LoginService loginService = Mockito.mock(LoginService.class);
        when(loginService.getAuthentication()).thenReturn(Observable.just(Mockito.mock(Authentication.class)));

        Dependencies.INSTANCE.setLoginService(loginService);
        Dependencies.INSTANCE.setUserService(Mockito.mock(UserService.class));
        Dependencies.INSTANCE.setChatService(chatService);
        Dependencies.INSTANCE.setChannelService(channelService);
        Dependencies.INSTANCE.setAnalytics(Mockito.mock(Analytics.class));
    }

    @Test
    public void channelWithEmptyNameCannotBeCreated() throws Exception {
        activity.launchActivity(new Intent());

        onView(withId(R.id.createButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void channelWithANameCanBeCreated() throws Exception {
        String name = "new channel";
        activity.launchActivity(new Intent());

        when(channelService.createPublicChannel(any(Channel.class))).thenReturn(Observable.just(new DatabaseResult<>(new Channel(name, false))));
        when(chatService.getChat(any(Channel.class))).thenReturn(Observable.just(new DatabaseResult<>(new Chat(new ArrayList<Message>()))));
        onView(withId(R.id.newChannelName)).perform(typeText(name));
        onView(withId(R.id.createButton)).perform(click());


        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText(name))).check(matches(isDisplayed()));
    }
}
