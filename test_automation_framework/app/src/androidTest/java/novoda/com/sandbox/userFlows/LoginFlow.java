package novoda.com.sandbox.userFlows;

import android.support.test.espresso.NoMatchingViewException;

import novoda.com.sandbox.R;
import novoda.com.sandbox.models.Credentials;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class LoginFlow {

    public void doLogin(Credentials credentials) {
        onView(withId(R.id.main_activity_sign_in_button)).perform(click());
        onView(withId(R.id.sign_in_activity_username_field))
                .perform(typeText(credentials.getUsername()));
        onView(withId(R.id.sign_in_activity_password_field))
                .perform(typeText(credentials.getPassword()));
        onView(withId(R.id.sign_in_activity_submit_button)).perform(click());
    }

    public boolean checkIfLoggedIn() {
        onView(withId(R.id.main_activity_sign_in_button)).check(matches(isDisplayed()));
        return true;
    }

    public boolean correctErrorDialogIsShown(String dialog) {
        onView(withText(dialog)).check(matches(isDisplayed()));
        return true;
    }

    public boolean isSignedIn() {
        try {
            onView(withId(R.id.main_activity_sign_in_button)).check(matches(withText("Sign out")));
            return true;
        } catch (NoMatchingViewException exception) {
            return false;
        }
    }
}
