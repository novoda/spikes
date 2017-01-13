package com.novoda.toggletalkback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;
import com.novoda.accessibility.Action;
import com.novoda.accessibility.Actions;

import java.util.Arrays;

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
        final Action actionClick = createActionClick(movie);
        final Action actionClickPlay = createActionClickPlay(movie);
        final Action actionClickFavorite = createActionClickFavorite(movie);

        Actions allActions = new Actions(Arrays.asList(actionClick, actionClickPlay, actionClickFavorite));

        nameTextView.setText(movie.name);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionClick.run();
            }
        });

        playButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionClickPlay.run();
            }
        });

        favoriteButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionClickFavorite.run();
            }
        });
    }

    private Action createActionClickFavorite(final Movie movie) {
        return new Action(R.id.action_click_favorite_movie, R.string.action_click_favorite_movie, new Runnable() {
            @Override
            public void run() {
                listener.onClickFavorite(movie);
            }
        });
    }

    private Action createActionClickPlay(final Movie movie) {
        return new Action(R.id.action_click_play_movie, R.string.action_click_play_movie, new Runnable() {
            @Override
            public void run() {
                listener.onClickPlay(movie);
            }
        });
    }

    private Action createActionClick(final Movie movie) {
        return new Action(R.id.action_click_movie, R.string.action_click_movie, new Runnable() {
            @Override
            public void run() {
                listener.onClick(movie);
            }
        });
    }

    public interface Listener {

        void onClick(Movie movie);

        void onClickPlay(Movie movie);

        void onClickFavorite(Movie movie);

    }

}
