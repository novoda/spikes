package com.novoda.androidskeleton;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.androidskeleton.MyActivity;
import com.novoda.androidskeleton.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {

    @Rule
    public ActivityTestRule<MyActivity> activityRule = new ActivityTestRule<>(MyActivity.class);

    @Test
    public void displaysAppBar() {
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }

}
