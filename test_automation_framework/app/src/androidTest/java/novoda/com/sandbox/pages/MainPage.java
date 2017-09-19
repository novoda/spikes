package novoda.com.sandbox.pages;

import android.support.test.espresso.ViewInteraction;

import novoda.com.sandbox.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainPage extends BasePage {

    private ViewInteraction SIGN_IN_BUTTON = onView(withId(R.id.main_activity_sign_in_button));

    public void openSignInPage() {
        SIGN_IN_BUTTON.perform(click());
    }

    public void validateLoggedInStatus() {
        SIGN_IN_BUTTON.check(matches(isDisplayed()));
        SIGN_IN_BUTTON.check(matches(withText("Sign out")));
    }
    
}
