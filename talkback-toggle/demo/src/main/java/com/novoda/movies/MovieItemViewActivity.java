package com.novoda.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;

public class MovieItemViewActivity extends AppCompatActivity {

    private MovieItemView movieItemView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_item_view);
        movieItemView = ButterKnife.findById(this, R.id.movie_item_view);

        movieItemView.attachListener(createMovieListener());
        movieItemView.bind(new Movie("Edward Scissorhands"));
    }

    private MovieItemView.Listener createMovieListener() {
        final TextView reactionTextView = ButterKnife.findById(this, R.id.reaction_text_view);

        return new MovieItemView.Listener() {
            @Override
            public void onClick(Movie movie) {
                reactionTextView.setText("onClick " + movie.name);
            }

            @Override
            public void onClickPlay(Movie movie) {
                reactionTextView.setText("onClickPlay " + movie.name);
            }

            @Override
            public void onClickFavorite(Movie movie) {
                reactionTextView.setText("onClickFavorite " + movie.name);
            }
        };
    }

    @Override
    protected void onDestroy() {
        movieItemView.detachListeners();
        super.onDestroy();
    }
}
