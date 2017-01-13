package com.novoda.toggletalkback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;
import com.novoda.accessibility.Action;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieItemView extends RelativeLayout {

    private final AccessibilityServices a11yServices;

    @BindView(R.id.movie_item_button_play)
    View playButtonView;

    @BindView(R.id.movie_item_button_favorite)
    View favoriteButtonView;

    @BindView(R.id.movie_item_text_name)
    TextView nameTextView;

    private Listener listener;

    public MovieItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        a11yServices = AccessibilityServices.newInstance(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_movie_item, this);
        ButterKnife.bind(this);
    }

    public void attachListener(Listener listener) {
        this.listener = listener;
    }

    public void detachListeners() {
        this.listener = null;
    }

    public void bind(final Movie movie) {
        final Action movieClickAction = new Action(R.id.action_click_movie, R.string.action_click_movie, new Runnable() {
            @Override
            public void run() {
                listener.onClick(movie);
            }
        });

        final Action movieClickPlayAction = new Action(R.id.action_click_play_movie, R.string.action_click_play_movie, new Runnable() {
            @Override
            public void run() {
                listener.onClickPlay(movie);
            }
        });

        nameTextView.setText(movie.name);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                movieClickAction.run();
            }
        });

        playButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                movieClickPlayAction.run();
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
