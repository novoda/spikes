package novoda.com.sandbox.pages;

import novoda.com.sandbox.R;
import novoda.com.sandbox.models.Credentials;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class SignInPage extends BasePage {

    public void doLogin(Credentials credentials) {
        onView(withId(R.id.sign_in_activity_username_field))
                .perform(typeText(credentials.getUsername()));
        onView(withId(R.id.sign_in_activity_password_field))
                .perform(typeText(credentials.getPassword()));
        onView(withId(R.id.sign_in_activity_submit_button)).perform(click());
    }

}
