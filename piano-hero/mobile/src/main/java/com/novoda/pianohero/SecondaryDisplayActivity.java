package com.novoda.pianohero;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class SecondaryDisplayActivity extends AppCompatActivity {

    private static final long GAME_DURATION_MILLIS = TimeUnit.SECONDS.toMillis(60);

    private GameMvp.Presenter gameMvpPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_secondary_display);

        GameMvp.Model gameMvpModel = createGameMvpModel();
        gameMvpPresenter = new GamePresenter(gameMvpModel, gameMvpView);
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
                createPiano(),
                createRestartGameClickable(),
                new GameTimer(GAME_DURATION_MILLIS),
                new ViewModelConverter(phrasesIterator, GAME_DURATION_MILLIS),
                new PlayAttemptGrader(),
                phrasesIterator
        );
    }

    private Piano createPiano() {
        return (C4ToB5ViewPiano) findViewById(R.id.piano_view);
    }

    private Clickable createRestartGameClickable() {
        final View restartButton = findViewById(R.id.game_timer_widget);
        final View gameStatus = findViewById(R.id.game_text_status);
        return new Clickable() {
            @Override
            public void setListener(final Listener listener) {
                gameStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (restartButton.getVisibility() == View.VISIBLE) {
                            return;
                        }
                        listener.onClick();
                    }
                });
                restartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick();
                    }
                });
            }
        };
    }

    private static class PresentationGameMvpView implements GameMvp.View {

        @Nullable
        private GameViewPresentation gameViewPresentation;

        void update(GameViewPresentation gameViewPresentation) {
            this.gameViewPresentation = gameViewPresentation;
        }

        @Override
        public void show(GameInProgressViewModel viewModel) {
            if (gameViewPresentation == null) {
                Log.d("!!!", "no secondary displays found");
                return;
            }
            gameViewPresentation.delegate.show(viewModel);
            showIfNotShowing();
        }

        @Override
        public void show(GameOverViewModel viewModel) {
            if (gameViewPresentation == null) {
                Log.d("!!!", "no secondary displays found");
                return;
            }
            gameViewPresentation.delegate.show(viewModel);
            showIfNotShowing();
        }

        private void showIfNotShowing() {
            if (gameViewPresentation == null) {
                throw new IllegalStateException("should have checked prior to this point whether presentation available");
            }

            if (!gameViewPresentation.isShowing()) {
                gameViewPresentation.show();
            }
        }
    }

    private static class GameViewPresentation extends Presentation {

        private GameMvp.View delegate;

        public GameViewPresentation(Context outerContext, Display display) {
            super(outerContext, display);
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
                    (C4ToB5ViewPiano) findViewById(R.id.piano_view),
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
