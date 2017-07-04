package com.example;

import android.util.Log;
import android.widget.SeekBar;

import com.example.movie.Movie;
import com.example.movie.MoviePlayer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

class Presenter {

    private static final int SEEK_BAR_MAX = 100;

    private final MoviePlayer moviePlayer;
    private final PlayerControlsUi playerControlsUi;
    private final MovieView movieView;
    private final ResumePoints resumePoints;

    private boolean userSeeking;

    Presenter(MoviePlayer moviePlayer, PlayerControlsUi playerControlsUi, MovieView movieView, ResumePoints resumePoints) {
        this.moviePlayer = moviePlayer;
        this.playerControlsUi = playerControlsUi;
        this.movieView = movieView;
        this.resumePoints = resumePoints;
    }

    void startPresenting(final Movie movie) {
        PlayerControlsUi.Callback playerControlsUiCallback = createPlayerControlsUiCallback(movie);
        playerControlsUi.setCallback(playerControlsUiCallback);

        MoviePlayer.Callback moviePlayerCallback = createMoviePlayerCallback();
        moviePlayer.load(movie, moviePlayerCallback);

        restoreResumePointOf(movie);
        if (resumePoints.getWasPlaying()) {
            moviePlayer.play();
        }
    }

    private PlayerControlsUi.Callback createPlayerControlsUiCallback(final Movie movie) {
        return new PlayerControlsUi.Callback() {

            @Override
            public void onClickSkipToStart() {
                moviePlayer.seekToMillis(0);

                PlayerControlsUi.ViewModel viewModel = createViewModel(0, movie.durationMillis());
                playerControlsUi.bind(viewModel);
            }

            @Override
            public void onClickJumpBack() {
                long newPosition = Math.max(0, moviePlayer.getCurrentPositionMillis() - TimeUnit.SECONDS.toMillis(10));
                moviePlayer.seekToMillis(newPosition);

                PlayerControlsUi.ViewModel viewModel = createViewModel(newPosition, movie.durationMillis());
                playerControlsUi.bind(viewModel);
            }

            @Override
            public void onClickJumpPlayPause() {
                if (moviePlayer.isPlaying()) {
                    moviePlayer.pause();
                } else {
                    moviePlayer.play();
                }

                // TODO: could update UI to show play/pause
            }

            @Override
            public void onClickJumpAhead() {
                long newPosition = Math.min(moviePlayer.getCurrentPositionMillis() + TimeUnit.SECONDS.toMillis(10), movie.durationMillis());
                moviePlayer.seekToMillis(newPosition);

                PlayerControlsUi.ViewModel viewModel = createViewModel(newPosition, movie.durationMillis());
                playerControlsUi.bind(viewModel);
            }

            @Override
            public void onClickSkipToEnd() {
                long end = movie.durationMillis();
                moviePlayer.seekToMillis(end);

                PlayerControlsUi.ViewModel viewModel = createViewModel(end, end);
                playerControlsUi.bind(viewModel);
            }

            @Override
            public void onSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long currentTime = movieTimeMillisFromSeekBar(seekBar, movie);
                PlayerControlsUi.ViewModel viewModel = createViewModel(currentTime, movie.durationMillis());
                playerControlsUi.bind(viewModel);
            }

            @Override
            public void onSeekBarStartTrackingTouch(SeekBar seekBar) {
                userSeeking = true;
            }

            @Override
            public void onSeekBarStopTrackingTouch(SeekBar seekBar) {
                userSeeking = false;

                long position = movieTimeMillisFromSeekBar(seekBar, movie);
                moviePlayer.seekToMillis(position);
            }
        };
    }

    private long movieTimeMillisFromSeekBar(SeekBar seekBar, Movie movie) {
        float percentage = 1f * seekBar.getProgress() / SEEK_BAR_MAX;
        return (long) (movie.durationMillis() * percentage);
    }

    private MoviePlayer.Callback createMoviePlayerCallback() {
        return new MoviePlayer.Callback() {
            @Override
            public void onMovieResumed() {
                Log.d("!!!", "onMovieResumed: ");
            }

            @Override
            public void onMoviePaused() {
                Log.d("!!!", "onMoviePaused: ");
            }

            @Override
            public void onMovieStopped() {
                Log.d("!!!", "onMovieStopped: ");
            }

            @Override
            public void onMoviePlaying(long currentPositionMillis, long totalLengthMillis) {
                Log.d("!!!", "onMoviePlaying: current: " + currentPositionMillis + ", total: " + totalLengthMillis);
                if (!userSeeking) {
                    PlayerControlsUi.ViewModel viewModel = createViewModel(currentPositionMillis, totalLengthMillis);  // TODO: this should be tappable to swap to time-remaining
                    playerControlsUi.bind(viewModel);
                }
            }

            @Override
            public void onMovieComplete() {
                Log.d("!!!", "onMovieComplete: ");
            }
        };
    }

    private void restoreResumePointOf(Movie movie) {
        moviePlayer.seekToMillis(resumePoints.getResumePoint());
        PlayerControlsUi.ViewModel viewModel = createViewModel(resumePoints.getResumePoint(), movie.durationMillis());
        playerControlsUi.bind(viewModel);
    }

    void stopPresenting() {
        resumePoints.setResumePoint(moviePlayer.getCurrentPositionMillis());
        resumePoints.setWasPlaying(moviePlayer.isPlaying());
        moviePlayer.stop();
    }

    private PlayerControlsUi.ViewModel createViewModel(long currentTimeMillis, long totalTimeMillis) {
        String currentTime = formatMinutesSeconds(currentTimeMillis);
        String totalTime = formatMinutesSeconds(totalTimeMillis);
        float done = 1f * currentTimeMillis / totalTimeMillis;
        int seekBarProgress = (int) (done * SEEK_BAR_MAX);
        return new PlayerControlsUi.ViewModel(currentTime, totalTime, seekBarProgress);
    }

    private String formatMinutesSeconds(long timeMillis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis - TimeUnit.MINUTES.toMillis(minutes));
        return String.format(Locale.US, "%02d", minutes) + ":" + String.format(Locale.US, "%02d", seconds);
    }

}
