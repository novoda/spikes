package com.novoda.movies;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.novoda.accessibility.AccessibilityServices;
import com.novoda.accessibility.Action;
import com.novoda.accessibility.Actions;
import com.novoda.accessibility.ActionsAccessibilityDelegate;
import com.novoda.accessibility.ActionsAlertDialogCreator;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieItemView extends RelativeLayout {

    private final AccessibilityServices a11yServices;
    private final ActionsAlertDialogCreator dialogCreator;

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
        dialogCreator = new ActionsAlertDialogCreator(getContext());
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

    public void bind(Movie movie) {
        final Action actionClick = createActionClick(movie);
        final Action actionClickPlay = createActionClickPlay(movie);
        final Action actionClickFavorite = createActionClickFavorite(movie);

        nameTextView.setText(movie.name);
        setContentDescription(movie.name);

        if (a11yServices.isSpokenFeedbackEnabled()) {
            playButtonView.setOnClickListener(null);
            playButtonView.setClickable(false);
        } else {
            playButtonView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickPlay.run();
                }
            });
        }

        if (a11yServices.isSpokenFeedbackEnabled()) {
            favoriteButtonView.setOnClickListener(null);
            favoriteButtonView.setClickable(false);
        } else {
            favoriteButtonView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickFavorite.run();
                }
            });
        }

        final Actions allActions = collateActions(actionClick, actionClickPlay, actionClickFavorite);
        ActionsAccessibilityDelegate accessibilityDelegate = new ActionsAccessibilityDelegate(getResources(), allActions);
        accessibilityDelegate.setClickLabel("see actions");
        accessibilityDelegate.setLongClickLabel(getResources().getString(actionClick.getLabel()).toLowerCase(Locale.UK));
        ViewCompat.setAccessibilityDelegate(this, accessibilityDelegate);

        if (a11yServices.isSpokenFeedbackEnabled()) {
            setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    actionClick.run();
                    return true;
                }
            });
        } else {
            setOnClickListener(null);
            setLongClickable(false);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a11yServices.isSpokenFeedbackEnabled()) {
                    AlertDialog alertDialog = dialogCreator.create(allActions);
                    alertDialog.show();
                } else {
                    actionClick.run();
                }
            }
        });
    }

    private Actions collateActions(Action... action) {
        return new Actions(Arrays.asList(action));
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
