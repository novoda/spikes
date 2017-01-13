package com.novoda.toggletalkback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;

public class MovieItemViewActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_item_view);
        MovieItemView movieItemView = ButterKnife.findById(this, R.id.movie_item_view);

        movieItemView.attachListener(new MovieItemView.Listener() {
            @Override
            public void onClick(Movie movie) {
                toast("onClick " + movie.name);
            }

            @Override
            public void onClickPlay(Movie movie) {
                toast("onClickPlay " + movie.name);
            }

            @Override
            public void onClickFavorite(Movie movie) {
                toast("onClickFavourite " + movie.name);
            }
        });

        movieItemView.bind(new Movie("Edward Scissorhands"));
    }

    private void toast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

}
