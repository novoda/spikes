/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novoda.todoapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;

import com.novoda.todoapp.tasks.TasksActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Tests for the {@link DrawerLayout} layout component in {@link TasksActivity} which manages
 * navigation within the app.
 */
@RunWith(AndroidJUnit4.class)
public class AppNavigationTest {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<TasksActivity> mActivityTestRule =
            new ActivityTestRule<TasksActivity>(TasksActivity.class);

    @Test
    public void clickOnStatisticsNavigationItem_ShowsStatisticsScreen() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.content))
                .check(matches(isClosed())) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start statistics screen.
        onView(withId(R.id.nav_drawer))
                .perform(navigateTo(R.id.statistics_nav_drawer_item));

        // Check that statistics Activity was opened.
        onView(withId(R.id.statistics_label)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnListNavigationItem_ShowsListScreen() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.content))
                .check(matches(isClosed())) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start statistics screen.
        onView(withId(R.id.nav_drawer))
                .perform(navigateTo(R.id.statistics_nav_drawer_item));

        // Open Drawer to click on navigation.
        onView(withId(R.id.content))
                .check(matches(isClosed())) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start tasks list screen.
        onView(withId(R.id.nav_drawer))
                .perform(navigateTo(R.id.to_do_list_nav_drawer_item));

        // Check that Tasks Activity was opened.
        onView(withId(R.id.tasks_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnAndroidHomeIcon_OpensNavigation() {
        // Check that left drawer is closed at startup
        onView(withId(R.id.content))
                .check(matches(isClosed())); // Left Drawer should be closed.

        // Open Drawer
        onView(withContentDescription(R.string.open_navigation)).perform(click());

        // Check if drawer is open
        onView(withId(R.id.content))
                .check(matches(isOpen())); // Left drawer is open open.
    }

}
