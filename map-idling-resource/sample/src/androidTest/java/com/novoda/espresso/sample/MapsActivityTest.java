package com.novoda.espresso.sample;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;

import com.novoda.espresso.MapIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MapsActivityTest {

    @Rule
    public ActivityTestRule<MapsActivity> activityTestRule = new ActivityTestRule<>(MapsActivity.class);
    private IdlingResource idlingResource;

    @Before
    public void setUp() {
        idlingResource = MapIdlingResource.from(activityTestRule.getActivity());
        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void shouldShowMyLocationButtonInActionBar() throws Throwable {
        onView(withId(R.id.my_location))
                .check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(idlingResource);
    }
}
