package com.novoda.bonfire.user;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.novoda.bonfire.R;
import com.novoda.bonfire.TestDependencies;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channel.Access;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.service.UserService;

import java.util.ArrayList;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class UsersActivityTest {

    private static final User AUTHENTICATED_USER = new User("logged_user_id", "Authenticated User", "http://invalid.photo/url");
    private static final ArrayList<User> ALL_USERS = new ArrayList<>();
    static {
        for (int i = 0; i < 5; i++) {
            ALL_USERS.add(new User("test_user_" + i, "Test User " + i, "http://non.existent.url/" + i));
        }
        ALL_USERS.add(AUTHENTICATED_USER);
    }

    private ArrayList<User> channelOwners;

    @Rule
    public final ActivityTestRule<UsersActivity> activity = new ActivityTestRule<>(UsersActivity.class, false, false);
    private ChannelService channelService;
    private PublishSubject<DatabaseResult<Users>> subject;

    @Before
    public void setUp() throws Exception {
        channelService = Mockito.mock(ChannelService.class);
        UserService userService = Mockito.mock(UserService.class);
        when(userService.getAllUsers()).thenReturn(Observable.just(new Users(ALL_USERS)));
        TestDependencies.init()
                .withChannelService(channelService)
                .withUserService(userService);

        channelOwners = new ArrayList<>();
        channelOwners.add(AUTHENTICATED_USER);
        channelOwnersUpdateAfterEveryChange();
    }

    @Test
    public void userIsAddedToOwnersWhenClicked() throws Exception {
        launchUsersActivity();

        clickOnUserAtPosition(1);

        onView(withId(R.id.users_recycler_view)).check(matches(atPosition(1, isSelected())));
    }

    @Test
    public void userIsRemovedFromOwnersIfHeWasOwner() throws Exception {
        channelOwners.add(ALL_USERS.get(2));
        launchUsersActivity();

        clickOnUserAtPosition(2);

        onView(withId(R.id.users_recycler_view)).check(matches(atPosition(2, not(isSelected()))));
    }

    private void launchUsersActivity() {
        activity.launchActivity(UsersActivity.createIntentFor(InstrumentationRegistry.getContext(), new Channel("test_name", Access.PRIVATE)));
        subject.onNext(new DatabaseResult<>(new Users(channelOwners)));
    }

    private void clickOnUserAtPosition(int position) {
        onView(withId(R.id.users_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    private Matcher<View> atPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    private void channelOwnersUpdateAfterEveryChange() {
        subject = PublishSubject.create();

        doAnswer(new Answer<Observable<DatabaseResult<Users>>>() {
            @Override
            public Observable<DatabaseResult<Users>> answer(InvocationOnMock invocation) throws Throwable {
                return subject.asObservable().observeOn(AndroidSchedulers.mainThread());
            }
        }).when(channelService).getOwnersOfChannel(any(Channel.class));

        doAnswer(new Answer<Observable<DatabaseResult<User>>>() {
            @Override
            public Observable<DatabaseResult<User>> answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[1];
                channelOwners.add(user);
                subject.onNext(new DatabaseResult<>(new Users(channelOwners)));
                return Observable.just(new DatabaseResult<>(user));
            }
        }).when(channelService).addOwnerToPrivateChannel(any(Channel.class), any(User.class));

        doAnswer(new Answer<Observable<DatabaseResult<User>>>() {
            @Override
            public Observable<DatabaseResult<User>> answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[1];
                channelOwners.remove(user);
                subject.onNext(new DatabaseResult<>(new Users(channelOwners)));
                return Observable.just(new DatabaseResult<>(user));
            }
        }).when(channelService).removeOwnerFromPrivateChannel(any(Channel.class), any(User.class));
    }
}
