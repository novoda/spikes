package com.example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.androidskeleton.R;
import com.example.movie.Movie;
import com.example.movie.MoviePlayer;

import butterknife.ButterKnife;

public class VideoPlayerActivity extends AppCompatActivity {

    private Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);

        MovieView movieView = ButterKnife.findById(this, R.id.movie_view);
        MoviePlayer moviePlayer = MoviePlayer.newInstance(new Handler(), movieView);
        PlayerControlsUi playerControlsUi = ButterKnife.findById(this, R.id.player_controls_ui);
        ResumePoints resumePoints = ResumePoints.create(this);
        presenter = new Presenter(moviePlayer, playerControlsUi, movieView, resumePoints);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.startPresenting(new Movie());
    }

    @Override
    protected void onPause() {
        presenter.stopPresenting();
        super.onPause();
    }

}
