package com.novoda.movies;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.talkbacktoggle.TalkBackActivityTestRule;

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
public class MovieItemViewTalkBackTest {

    @Rule
    public ActivityTestRule<MovieItemViewActivity> activityRule = new TalkBackActivityTestRule<>(MovieItemViewActivity.class);

    @Test
    public void clickMovieItemViewDialog_openDetails() {
        onView(withId(R.id.movie_item_view)).perform(click());

        onView(withText("Open details")).perform(click());

        checkReactionMatches("onClick Edward Scissorhands");
    }

    @Test
    public void clickMovieItemViewDialog_favorite() {
        onView(withId(R.id.movie_item_view)).perform(click());

        onView(withText("Favorite")).perform(click());

        checkReactionMatches("onClickFavorite Edward Scissorhands");
    }

    @Test
    public void clickMovieItemViewDialog_play() {
        onView(withId(R.id.movie_item_view)).perform(click());

        onView(withText("Play")).perform(click());

        checkReactionMatches("onClickPlay Edward Scissorhands");
    }

    private void checkReactionMatches(String text) {
        onView(withId(R.id.reaction_text_view)).check(matches(withText(text)));
    }

}
