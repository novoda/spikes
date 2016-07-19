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

package com.novoda.todoapp.statistics;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.data.model.Task;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for the statistics screen.
 */
@RunWith(AndroidJUnit4.class)
public class StatisticsScreenTest {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<StatisticsActivity> mStatisticsActivityTestRule =
            new ActivityTestRule<>(StatisticsActivity.class, true, false);

    @Before
    public void intentWithStubbedTaskId() {
        TodoApplication.TASKS_SERVICE.deleteAllTasks();
        SystemClock.sleep(3000); //Issue with deletion of tasks with real service being a bit long.
        // Can be fixed if we encounter more issues and move to tests against a test service.

        // Given some tasks
        TodoApplication.TASKS_SERVICE.save(Task.builder().id(Id.from("99")).title("Title1").build());
        TodoApplication.TASKS_SERVICE.save(Task.builder().id(Id.from("999")).title("Title2").isCompleted(true).build());
        SystemClock.sleep(500);

        // Lazily start the Activity from the ActivityTestRule
        Intent startIntent = new Intent();
        mStatisticsActivityTestRule.launchActivity(startIntent);
    }

    @Test
    public void Tasks_ShowsNonEmptyMessage() throws Exception {
        // Check that the active and completed tasks text is displayed
        onView(withText(equalTo("Active tasks: 1\nCompleted tasks: 1"))).check(matches(isDisplayed()));
    }
}
