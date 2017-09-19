package novoda.com.sandbox.pages;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public abstract class BasePage {
    public boolean validateErrorDialog(String dialog) {
        onView(withText(dialog)).check(matches(isDisplayed()));
        return true;
    }
}
