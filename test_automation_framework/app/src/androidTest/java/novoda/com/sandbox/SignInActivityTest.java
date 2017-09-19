package novoda.com.sandbox;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import novoda.com.sandbox.models.User;
import novoda.com.sandbox.services.UserService;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {
    private UserService userService;

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws Exception {
        userService = new UserService();
    }

    @Test
    public void signIn_ValidCredentials_loginPossible() {
        User validUser = userService.getValidUser();

        onView(withId(R.id.main_activity_sign_in_button)).perform(click());
        onView(withId(R.id.sign_in_activity_username_field))
                .perform(typeText(validUser.getCredentials().getUsername()));
        onView(withId(R.id.sign_in_activity_password_field))
                .perform(typeText(validUser.getCredentials().getPassword()));
        onView(withId(R.id.sign_in_activity_submit_button)).perform(click());

        onView(withId(R.id.main_activity_sign_in_button)).check(matches(isDisplayed()));
    }

    @Test
    public void signIn_UsernameTooShort_loginNotPossible() throws InterruptedException {
        User usernameTooShortUser = userService.getUserWithTooShortUsername();

        onView(withId(R.id.main_activity_sign_in_button)).perform(click());
        onView(withId(R.id.sign_in_activity_username_field))
                .perform(typeText(usernameTooShortUser.getCredentials().getUsername()));
        onView(withId(R.id.sign_in_activity_password_field))
                .perform(typeText(usernameTooShortUser.getCredentials().getPassword()));
        onView(withId(R.id.sign_in_activity_submit_button)).perform(click());
        Thread.sleep(20);

        onView(withText("Oops something went wrong, is your username and password more than 4 characters?")).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        Application.setSignedOut();
    }
}
