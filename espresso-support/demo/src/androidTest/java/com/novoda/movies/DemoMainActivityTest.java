package com.novoda.movies;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemoMainActivityTest {

    @Rule
    public ActivityTestRule<DemoMainActivity> activityRule = new ActivityTestRule<>(DemoMainActivity.class);

    @Test
    public void testActivityNotNull() {
        if (activityRule.getActivity() == null) {
            throw new AssertionError("activity null");
        }
    }
}
