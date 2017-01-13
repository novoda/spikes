package com.novoda.toggletalkback;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {

    @Rule
    public ActivityTestRule<MovieItemViewActivity> activityRule = new ActivityTestRule<>(MovieItemViewActivity.class);

    @Test
    public void clickMovieItemView() {
        onView(withId(R.id.movie_item_view)).perform(click());

        checkReactionMatches("onClick Edward Scissorhands");
    }

    @Test
    public void clickMovieFavoriteView() {
        onView(withId(R.id.movie_item_button_favorite)).perform(click());

        checkReactionMatches("onClickFavorite Edward Scissorhands");
    }

    @Test
    public void clickMoviePlayView() {
        onView(withId(R.id.movie_item_button_play)).perform(click());

        checkReactionMatches("onClickPlay Edward Scissorhands");
    }

    private void checkReactionMatches(String text) {
        onView(withId(R.id.reaction_text_view)).check(matches(withText(text)));
    }

}
