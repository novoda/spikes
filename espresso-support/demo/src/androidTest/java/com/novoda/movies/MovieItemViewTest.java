package com.novoda.movies;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.novoda.espresso.HitCounter;
import com.novoda.espresso.ViewActivityRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieItemViewTest {

    private static final Movie EDWARD_SCISSORHANDS = new Movie("Edward Scissorhands");
    private static final Movie NOT_EDWARD_SCISSORHANDS = new Movie("NOT Edward Scissorhands");

    @Rule
    public ViewActivityRule<MovieItemView> viewActivityRule = new ViewActivityRule<>(R.layout.test_movie_item_view);

    private HitCountingMovieItemViewListener hitCountingListener;

    @Before
    public void setUp() {
        MovieItemView view = viewActivityRule.getView();

        hitCountingListener = new HitCountingMovieItemViewListener();
        view.attachListener(hitCountingListener);

        // setup standard 'given' used for all tests
        viewActivityRule.bindViewUsing(new ViewActivityRule.Binder<MovieItemView>() {
            @Override
            public void bind(MovieItemView view) {
                view.bind(EDWARD_SCISSORHANDS);
            }
        });
    }

    @After
    public void tearDown() {
        viewActivityRule.getView().detachListeners();
    }

    @Test
    public void clickMovieItemView() {
        onView(withClassName(is(MovieItemView.class.getName()))).perform(click());

        hitCountingListener.assertClick(EDWARD_SCISSORHANDS);
    }

    @Test
    public void clickMovieFavoriteView() {
        onView(withId(R.id.movie_item_button_favorite)).perform(click());

        hitCountingListener.assertClickFavourite(EDWARD_SCISSORHANDS);
    }

    @Test
    public void clickMoviePlayView() {
        onView(withId(R.id.movie_item_button_play)).perform(click());

        hitCountingListener.assertClickPlay(EDWARD_SCISSORHANDS);
    }

    @Test
    public void rebindWithDifferentMovie_noClickMoviePlayView() {
        // setup 'given' in test
        viewActivityRule.bindViewUsing(new ViewActivityRule.Binder<MovieItemView>() {
            @Override
            public void bind(MovieItemView view) {
                view.bind(NOT_EDWARD_SCISSORHANDS);
            }
        });

        onView(withId(R.id.movie_item_button_play)).perform(click());

        // like `Mockito.verify(foo, never())`
        hitCountingListener.assertNoClickPlay(EDWARD_SCISSORHANDS);
    }

    private static class HitCountingMovieItemViewListener implements MovieItemView.Listener {

        private static final String ON_CLICK = "onClick ";
        private static final String ON_CLICK_PLAY = "onClickPlay ";
        private static final String ON_CLICK_FAVORITE = "onClickFavorite ";

        private final HitCounter hitCounter = new HitCounter();

        @Override
        public void onClick(Movie movie) {
            hitCounter.markHit(ON_CLICK + movie.name);
        }

        public void assertClick(Movie movie) {
            hitCounter.assertHit(ON_CLICK + movie.name);
        }

        public void assertNoClick(Movie movie) {
            hitCounter.assertNoHit(ON_CLICK + movie.name);
        }

        @Override
        public void onClickPlay(Movie movie) {
            hitCounter.markHit(ON_CLICK_PLAY + movie.name);
        }

        public void assertClickPlay(Movie movie) {
            hitCounter.assertHit(ON_CLICK_PLAY + movie.name);
        }

        public void assertNoClickPlay(Movie movie) {
            hitCounter.assertNoHit(ON_CLICK_PLAY + movie.name);
        }

        @Override
        public void onClickFavorite(Movie movie) {
            hitCounter.markHit(ON_CLICK_FAVORITE + movie.name);
        }

        public void assertClickFavourite(Movie movie) {
            hitCounter.assertHit(ON_CLICK_FAVORITE + movie.name);
        }

        public void assertNoClickFavourite(Movie movie) {
            hitCounter.assertNoHit(ON_CLICK_FAVORITE + movie.name);
        }

    }

}
