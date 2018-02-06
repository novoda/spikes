package com.novoda.androidstoreexample

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import com.novoda.androidstoreexample.adapters.CategoryAdapter
import org.hamcrest.Description
import org.hamcrest.Matcher

object ViewMatchers {

    @JvmStatic
    fun withCategoryTitle(title: String): Matcher<RecyclerView.ViewHolder> {
        return object : BoundedMatcher<RecyclerView.ViewHolder, CategoryAdapter.Holder>(CategoryAdapter.Holder::class.java) {
            override fun matchesSafely(item: CategoryAdapter.Holder): Boolean {
                return item.categoryName.toString().equals(title, true)
            }

            override fun describeTo(description: Description) {
                description.appendText("view holder with title: " + title)
            }
        }
    }
}