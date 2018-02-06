package com.novoda.androidstoreexample

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.novoda.androidstoreexample.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EspressoTestExample {

    private val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)


    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = activityTestRule


    @Test
    fun firstNavigationTest() {
        val categoryMatcher = ViewMatchers.withCategoryTitle("HATS")

        onView(withId(R.id.categoryListView)).perform(scrollToHolder(categoryMatcher), actionOnHolderItem(categoryMatcher, click()))

        val productMatcher = ViewMatchers.withProductTitle("hat white")

        onView(withId(R.id.productListView)).perform(scrollToHolder(productMatcher), actionOnHolderItem(productMatcher, click()))

        onView(withId(R.id.productDetailDescription)).check(matches(isDisplayed()))
    }
}
