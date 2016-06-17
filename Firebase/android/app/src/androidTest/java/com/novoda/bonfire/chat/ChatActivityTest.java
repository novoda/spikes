package com.novoda.bonfire.chat;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.bonfire.R;
import com.novoda.bonfire.TestDependencies;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channel.Access;
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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {

    private static final User AUTHENTICATED_USER = new User("user_id", "Test User", "http://invalid.url");
    private static final ArrayList<Message> MESSAGES = new ArrayList<>();
    private static final DatabaseResult<Chat> CHAT = new DatabaseResult<>(new Chat(MESSAGES));
    private static final String CHANNEL_NAME = "test_channel";

    @Rule
    public final ActivityTestRule<ChatActivity> activity = new ActivityTestRule<>(ChatActivity.class, false, false);
    private ChatService chatService;

    @Before
    public void setUp() throws Exception {
        chatService = Mockito.mock(ChatService.class);
        LoginService loginService = Mockito.mock(LoginService.class);
        when(loginService.getAuthentication()).thenReturn(Observable.just(new Authentication(AUTHENTICATED_USER)));

        TestDependencies.init()
                .withChatService(chatService)
                .withLoginService(loginService);
    }

    @Test
    public void channelNameIsShownInToolbar() throws Exception {
        givenChatIsEmpty();
        launchChatActivityWithAccess(Access.PRIVATE);

        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText(CHANNEL_NAME))).check(matches(isDisplayed()));
    }

    @Test
    public void cannotManageUsersInPublicChannel() throws Exception {
        givenChatIsEmpty();
        launchChatActivityWithAccess(Access.PUBLIC);

        onView(withId(R.id.toolbar)).check(matches(not(hasDescendant(withId(R.id.manageOwners)))));
    }

    @Test
    public void canManageUsersWhenChannelIsPrivate() throws Exception {
        givenChatIsEmpty();
        launchChatActivityWithAccess(Access.PRIVATE);

        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withId(R.id.manageOwners))).check(matches(isDisplayed()));
    }

    @Test
    public void submitButtonIsDisabledWhenMessageIsEmpty() throws Exception {
        givenChatIsEmpty();
        launchChatActivityWithAccess(Access.PUBLIC);

        onView(withId(R.id.submit_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.message_edit)).perform(typeText("random message text"), closeSoftKeyboard(), clearText());
        onView(withId(R.id.submit_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void submitButtonIsEnabledWhenThereIsAMessage() throws Exception {
        givenChatIsEmpty();
        launchChatActivityWithAccess(Access.PRIVATE);

        onView(withId(R.id.message_edit)).perform(typeText("not so random message text"), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).check(matches(isEnabled()));
    }

    private void givenChatIsEmpty() {
        when(chatService.getChat(any(Channel.class))).thenReturn(Observable.just(CHAT));
    }

    @Test
    public void submittedMessageShowsInTheChat() throws Exception {
        givenChatUpdatesAfterEveryMessage();
        launchChatActivityWithAccess(Access.PRIVATE);

        String message = "test message";
        submitMessage(message);

        assertThatMessageFromUserIsShownInChat(AUTHENTICATED_USER.getName(), message);
    }

    private void launchChatActivityWithAccess(Access access) {
        activity.launchActivity(ChatActivity.createIntentFor(InstrumentationRegistry.getContext(), new Channel(CHANNEL_NAME, access)));
    }

    private void givenChatUpdatesAfterEveryMessage() {
        final PublishSubject<DatabaseResult<Chat>> subject = PublishSubject.create();

        doAnswer(new Answer<Observable<DatabaseResult<Chat>>>() {
            @Override
            public Observable<DatabaseResult<Chat>> answer(InvocationOnMock invocation) throws Throwable {
                return subject.asObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(CHAT);
            }
        }).when(chatService).getChat(any(Channel.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MESSAGES.add((Message) invocation.getArguments()[1]);
                subject.onNext(CHAT);
                return null;
            }
        }).when(chatService).sendMessage(any(Channel.class), any(Message.class));
    }

    private void assertThatMessageFromUserIsShownInChat(String userName, String message) {
        onView(withId(R.id.message_author_image)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.message_body), withText(equalToIgnoringCase(message)))).check(matches(isDisplayed()));
    }

    private void submitMessage(String message) {
        onView(withId(R.id.message_edit)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
    }
}
