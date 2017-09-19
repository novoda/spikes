package novoda.com.sandbox;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void signIn_ValidCredentials_loginPossible() {
        onView(withId(R.id.main_activity_sign_in_button)).perform(click());
        onView(withId(R.id.sign_in_activity_username_field)).perform(typeText("Username"));
        onView(withId(R.id.sign_in_activity_password_field)).perform(typeText("Password"));
        onView(withId(R.id.sign_in_activity_submit_button)).perform(click());
        onView(withId(R.id.main_activity_sign_in_button)).check(matches(isDisplayed()));
        Application.setSignedOut();
    }

    @Test
    public void signIn_UsernameTooShort_loginNotPossible() throws InterruptedException {
        onView(withId(R.id.main_activity_sign_in_button)).perform(click());
        onView(withId(R.id.sign_in_activity_username_field)).perform(typeText("u"));
        onView(withId(R.id.sign_in_activity_password_field)).perform(typeText("Password"));
        onView(withId(R.id.sign_in_activity_submit_button)).perform(click());
        Thread.sleep(20);
        onView(withText("Oops something went wrong, is your username and password more than 4 characters?")).check(matches(isDisplayed()));
    }
}
