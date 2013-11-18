package com.novoda.espressoemptylistview.test;

import android.test.ActivityInstrumentationTestCase2;

import com.novoda.espressoemptyviewlistview.MainActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.Matchers.*;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void test_grid_items_are_enabled() {
        onData(is(instanceOf(String.class))).atPosition(0).check(matches(isEnabled()));

        onData(allOf(is(instanceOf(String.class)), startsWith("Lorem ipsum"))).check(matches(isEnabled()));
    }
}
