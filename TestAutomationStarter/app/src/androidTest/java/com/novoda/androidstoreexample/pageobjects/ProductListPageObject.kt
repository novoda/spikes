package com.novoda.androidstoreexample.pageobjects

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.novoda.androidstoreexample.R
import com.novoda.androidstoreexample.ViewMatchers


class ProductListPageObject {

    fun navigateToProductDetails() {
        val productMatcher = ViewMatchers.withProductTitle("hat white")

        Espresso.onView(android.support.test.espresso.matcher.ViewMatchers.withId(R.id.productListView)).perform(RecyclerViewActions.scrollToHolder(productMatcher), RecyclerViewActions.actionOnHolderItem(productMatcher, ViewActions.click()))
    }

    fun assertProductDetailsDisplayed() {
        onView(withId(R.id.productDetailDescription)).check(matches(isDisplayed()))
    }
}
