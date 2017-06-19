package com.novoda.movies;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.espresso.TalkBackViewTestRule;
import com.novoda.espresso.ViewTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.novoda.espresso.AccessibilityViewMatchers.withUsageHintOnClick;
import static com.novoda.espresso.AccessibilityViewMatchers.withUsageHintOnLongClick;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieItemViewTalkBackTest {

    private static final Movie EDWARD_SCISSORHANDS = new Movie("Edward Scissorhands");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ViewTestRule<MovieItemView> viewTestRule = new TalkBackViewTestRule<>(R.layout.test_movie_item_view);

    @Mock
    MovieItemView.Listener movieItemListener;

    @Before
    public void setUp() {
        MovieItemView view = viewTestRule.getView();

        view.attachListener(movieItemListener);

        givenMovieItemViewIsBoundTo(EDWARD_SCISSORHANDS);
    }

    @After
    public void tearDown() {
        viewTestRule.getView().detachListeners();
    }

    @Test
    public void clickMovieItemView() {
        onView(withClassName(is(MovieItemView.class.getName()))).perform(click());

        checkMenuDisplayed();
    }

    @Test
    public void movieItemViewHasCustomUsageHintOnClick() {
        onView(withClassName(is(MovieItemView.class.getName())))
                .check(matches(withUsageHintOnClick("see actions")));
    }

    @Test
    public void movieItemViewHasCustomUsageHintOnLongClick() {
        onView(withClassName(is(MovieItemView.class.getName())))
                .check(matches(withUsageHintOnLongClick("open details")));
    }

    private void givenMovieItemViewIsBoundTo(final Movie movie) {
        viewTestRule.runOnMainSynchronously(new ViewTestRule.Runner<MovieItemView>() {
            @Override
            public void run(MovieItemView view) {
                view.bind(movie);
            }
        });
    }

    private void checkMenuDisplayed() {
        checkViewsWithTextDisplayed(
                R.string.action_click_movie,
                R.string.action_click_play_movie,
                R.string.action_click_favorite_movie
        );
    }

    private void checkViewsWithTextDisplayed(int... ids) {
        for (int id : ids) {
            onView(withText(id)).check(matches(isDisplayed()));
        }
    }

}
