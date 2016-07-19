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

package com.novoda.todoapp.task.newTask;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.todoapp.R;
import com.novoda.todoapp.task.newtask.NewTaskActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Tests for the add task screen.
 */
@RunWith(AndroidJUnit4.class)
public class NewTaskScreenTest {

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<NewTaskActivity> mAddTaskIntentsTestRule =
            new IntentsTestRule<>(NewTaskActivity.class);


    @Test
    public void errorShownOnEmptyTask() {
        // Add task title and description
        onView(withId(R.id.task_title)).perform(typeText(""));
        onView(withId(R.id.task_description)).perform(typeText(""),
                closeSoftKeyboard());
        // Save the task
        onView(withId(R.id.fab_task_done)).perform(click());

        // Verify empty tasks snackbar is shown
        String emptyTaskMessageText = getTargetContext().getString(R.string.empty_task_message);
        onView(withText(emptyTaskMessageText)).check(matches(isDisplayed()));
    }

}
