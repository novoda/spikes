package com.novoda.movies;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MovieItemView extends RelativeLayout {

    private View playButtonView;

    private View favoriteButtonView;

    private TextView nameTextView;

    private Listener listener;

    public MovieItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_movie_item, this);
        playButtonView = findViewById(R.id.movie_item_button_play);
        favoriteButtonView = findViewById(R.id.movie_item_button_favorite);
        nameTextView = ((TextView) findViewById(R.id.movie_item_text_name));
    }

    public void attachListener(Listener listener) {
        this.listener = listener;
    }

    public void detachListeners() {
        this.listener = null;
    }

    public void bind(final Movie movie) {
        nameTextView.setText(movie.name);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(movie);
            }
        });

        playButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickPlay(movie);
            }
        });

        favoriteButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickFavorite(movie);
            }
        });
    }

    public interface Listener {

        void onClick(Movie movie);

        void onClickPlay(Movie movie);

        void onClickFavorite(Movie movie);

    }

}
