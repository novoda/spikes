package com.example.movie;

import android.graphics.Canvas;
import android.os.Handler;

import com.example.MovieView;

public class MoviePlayer {

    private static final long FRAME_DURATION_MILLIS = 16;

    private final Timer timer;
    private final MovieView view;


    private Movie movie;
    private Callback callback;
    private long currentPositionMillis;
    private boolean shouldPlay;

    public static MoviePlayer newInstance(Handler handler, MovieView view) {
        Timer timer = new Timer(handler);
        return new MoviePlayer(timer, view);
    }

    private MoviePlayer(Timer timer, MovieView view) {
        this.timer = timer;
        this.view = view;
    }

    public void load(Movie movie, Callback callback) {
        timer.stop();

        this.movie = movie;
        this.callback = callback;

        setCurrentPositionMillis(0);
        this.shouldPlay = false;
    }

    public boolean isPlaying() {
        return shouldPlay;
    }

    public void play() {
        this.shouldPlay = true;

        callback.onMovieResumed();
        playSomeMore();
        updateMovieView();
    }

    private void playSomeMore() {
        if (currentPositionMillis >= movie.durationMillis()) {
            shouldPlay = false;
            callback.onMovieComplete();
        } else {
            timer.stop();
            timer.schedule(onFramePlayedCallback, FRAME_DURATION_MILLIS);
        }
    }

    private final Timer.Callback onFramePlayedCallback = new Timer.Callback() {
        @Override
        public void onCountdownComplete() {
            setCurrentPositionMillis(currentPositionMillis + FRAME_DURATION_MILLIS);
            if (currentPositionMillis < movie.durationMillis()) {
                callback.onMoviePlaying(currentPositionMillis, movie.durationMillis());
            } else {
                callback.onMoviePlaying(movie.durationMillis(), movie.durationMillis());
            }

            if (shouldPlay) {
                playSomeMore();
            }
        }
    };

    public void pause() {
        timer.stop();

        this.shouldPlay = false;
        callback.onMoviePaused();
        updateMovieView();
    }

    public void seekToMillis(long millis) {
        setCurrentPositionMillis(millis);
    }

    private void setCurrentPositionMillis(long millis) {
        this.currentPositionMillis = Math.max(0, Math.min(movie.durationMillis(), millis));
        updateMovieView();
    }

    public long getCurrentPositionMillis() {
        return currentPositionMillis;
    }

    public void stop() {
        timer.stop();

        this.shouldPlay = false;
        setCurrentPositionMillis(0);
        callback.onMovieStopped();
    }

    public void updateMovieView() {
        view.bind(movie.text(currentPositionMillis), movie.color(currentPositionMillis));
    }

    public interface Callback {

        void onMovieResumed();

        void onMoviePaused();

        void onMovieStopped();

        void onMovieComplete();

        /**
         * Called repeatedly whilst movie is in the playing state.
         * <p>
         * Use this to update your UI (e.g. time remaining, seekbar, etc.)
         *
         * @param currentPositionMillis
         * @param totalLengthMillis
         */
        void onMoviePlaying(long currentPositionMillis, long totalLengthMillis);

    }

}
