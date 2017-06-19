package com.novoda.movies;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieItemViewTest {

    private static final Movie EDWARD_SCISSORHANDS = new Movie("Edward Scissorhands");
    private static final Movie NOT_EDWARD_SCISSORHANDS = new Movie("NOT Edward Scissorhands");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ViewTestRule<MovieItemView> viewTestRule = new ViewTestRule<>(R.layout.test_movie_item_view);

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

        verify(movieItemListener).onClick(eq(EDWARD_SCISSORHANDS));
    }

    @Test
    public void clickMovieFavoriteView() {
        onView(withId(R.id.movie_item_button_favorite)).perform(click());

        verify(movieItemListener).onClickFavorite(eq(EDWARD_SCISSORHANDS));
    }

    @Test
    public void clickMoviePlayView() {
        onView(withId(R.id.movie_item_button_play)).perform(click());

        verify(movieItemListener).onClickPlay(eq(EDWARD_SCISSORHANDS));
    }

    @Test
    public void rebindWithDifferentMovie_noClickMoviePlayView() {
        givenMovieItemViewIsBoundTo(NOT_EDWARD_SCISSORHANDS);

        onView(withId(R.id.movie_item_button_play)).perform(click());

        verify(movieItemListener, never()).onClickPlay(eq(EDWARD_SCISSORHANDS));
    }

    private void givenMovieItemViewIsBoundTo(final Movie movie) {
        viewTestRule.runOnMainSynchronously(new ViewTestRule.Runner<MovieItemView>() {
            @Override
            public void run(MovieItemView view) {
                view.bind(movie);
            }
        });
    }

}
