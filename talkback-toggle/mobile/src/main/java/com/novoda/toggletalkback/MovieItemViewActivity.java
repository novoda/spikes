package com.novoda.toggletalkback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;

public class MovieItemViewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_item_view);
        MovieItemView movieItemView = ButterKnife.findById(this, R.id.movie_item_view);
        final TextView reactionTextView = ButterKnife.findById(this, R.id.reaction_text_view);

        movieItemView.attachListener(new MovieItemView.Listener() {
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
        });

        movieItemView.bind(new Movie("Edward Scissorhands"));
    }

}
