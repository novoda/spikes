package com.novoda.pianohero;

import android.app.Presentation;
import android.content.Context;
import android.media.MediaRouter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class SecondaryDisplayActivity extends AppCompatActivity {

    private static final long GAME_DURATION_MILLIS = TimeUnit.SECONDS.toMillis(60);

    private GameMvp.Presenter gameMvpPresenter;

    private C4ToB5ViewPiano pianoWidget;
    private Button resetButton;
    private PresentationGameMvpView presentationGameMvpView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_secondary_display);

        resetButton = (Button) findViewById(R.id.secondary_display_restart_button);
        pianoWidget = (C4ToB5ViewPiano) findViewById(R.id.secondary_display_piano_widget);

        GameMvp.Model gameMvpModel = createGameMvpModel();
        presentationGameMvpView = new PresentationGameMvpView();
        gameMvpPresenter = new GamePresenter(gameMvpModel, presentationGameMvpView);

        MediaRouter mediaRouter = (MediaRouter) getSystemService(MEDIA_ROUTER_SERVICE);
        mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, new MediaRouter.SimpleCallback() {
            @Override
            public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                Display display = getDisplayFrom(info);
                if (display == null) {
                    presentationGameMvpView.update(null);
                } else {
                    presentationGameMvpView.update(new GameSecondaryDisplayPresentation(SecondaryDisplayActivity.this, display, pianoWidget));
                }
            }

            @Nullable
            private Display getDisplayFrom(@Nullable MediaRouter.RouteInfo info) {
                return info == null ? null : info.getPresentationDisplay();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameMvpPresenter.startPresenting();
    }

    @Override
    protected void onPause() {
        gameMvpPresenter.stopPresenting();
        super.onPause();
    }

    private GameMvp.Model createGameMvpModel() {
        PhrasesIterator phrasesIterator = new PhrasesIterator();
        return new GameModel(
                new SongSequencePlaylist(),
                pianoWidget,
                new Clickable() {
                    @Override
                    public void setListener(final Listener listener) {
                        resetButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onClick();
                            }
                        });
                    }
                },
                new GameTimer(GAME_DURATION_MILLIS),
                new ViewModelConverter(phrasesIterator, GAME_DURATION_MILLIS),
                new PlayAttemptGrader(),
                phrasesIterator
        );
    }

    private static class PresentationGameMvpView implements GameMvp.View {

        @Nullable
        private GameSecondaryDisplayPresentation gameSecondaryDisplayPresentation;

        void update(GameSecondaryDisplayPresentation gameSecondaryDisplayPresentation) {
            this.gameSecondaryDisplayPresentation = gameSecondaryDisplayPresentation;
        }

        @Override
        public void show(GameInProgressViewModel viewModel) {
            if (gameSecondaryDisplayPresentation == null) {
                Log.d("!!!", "no secondary displays found");
                return;
            }
            showIfNotShowing();
            gameSecondaryDisplayPresentation.delegate.show(viewModel);
        }

        @Override
        public void show(GameOverViewModel viewModel) {
            if (gameSecondaryDisplayPresentation == null) {
                Log.d("!!!", "no secondary displays found");
                return;
            }
            showIfNotShowing();
            gameSecondaryDisplayPresentation.delegate.show(viewModel);
        }

        private void showIfNotShowing() {
            if (gameSecondaryDisplayPresentation == null) {
                throw new IllegalStateException("should have checked prior to this point whether presentation available");
            }

            if (!gameSecondaryDisplayPresentation.isShowing()) {
                gameSecondaryDisplayPresentation.show();
            }
        }
    }

    private static class GameSecondaryDisplayPresentation extends Presentation {

        private final C4ToB5ViewPiano pianoWidget;

        private GameMvp.View delegate;

        GameSecondaryDisplayPresentation(Context outerContext, Display display, C4ToB5ViewPiano pianoWidget) {
            super(outerContext, display);
            this.pianoWidget = pianoWidget;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.presentation_secondary_display);
            delegate = createGameMvpView();
        }

        private GameMvp.View createGameMvpView() {
            return new AndroidGameMvpView(
                    createSpeaker(),
                    pianoWidget,
                    createScoreDisplayer(),
                    (C4ToB5TrebleStaffWidget) findViewById(R.id.game_widget_treble_staff),
                    (TextView) findViewById(R.id.game_text_status),
                    (TimerWidget) findViewById(R.id.game_timer_widget)
            );
        }

        private Speaker createSpeaker() {
            return new AndroidSynthSpeaker();
        }

        private ScoreDisplayer createScoreDisplayer() {
            final TextView scoreTextView = ((TextView) findViewById(R.id.score_text_view));
            scoreTextView.setVisibility(View.VISIBLE);
            return new ScoreDisplayer() {
                @Override
                public void display(CharSequence score) {
                    scoreTextView.setText(score);
                    scoreTextView.setVisibility(View.VISIBLE);
                }

                @Override
                public void hide() {
                    scoreTextView.setVisibility(View.GONE);
                }
            };
        }
    }
}
