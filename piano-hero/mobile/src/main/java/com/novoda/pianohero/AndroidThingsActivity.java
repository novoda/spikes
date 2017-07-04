package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class AndroidThingsActivity extends AppCompatActivity {

    private static final long GAME_DURATION_MILLIS = TimeUnit.SECONDS.toMillis(60);

    private AndroidThingThings androidThingThings = new AndroidThingThings();
    private GameMvp.Presenter gameMvpPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        GameMvp.Model gameMvpModel = createGameMvpModel();
        GameMvp.View gameMvpView = createGameMvpView();
        gameMvpPresenter = new GamePresenter(gameMvpModel, gameMvpView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        androidThingThings.open();
        gameMvpPresenter.startPresenting();
    }

    @Override
    protected void onPause() {
        gameMvpPresenter.stopPresenting();
        androidThingThings.close();
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
        KeyStationMini32Piano keyStationMini32Piano = new KeyStationMini32Piano(this);
        androidThingThings.add(keyStationMini32Piano);

        if (isThingsDevice()) {
            return keyStationMini32Piano;
        } else {
            C4ToB5ViewPiano virtualPianoView = (C4ToB5ViewPiano) findViewById(R.id.piano_view);
            return new CompositePiano(virtualPianoView, keyStationMini32Piano);
        }
    }

    private Clickable createRestartGameClickable() {
        final View restartButton = findViewById(R.id.game_timer_widget);
        final View gameStatus = findViewById(R.id.game_text_status);
        if (isThingsDevice()) {
            final GpioButtonClickable gpioButtonClickable = new GpioButtonClickable();
            androidThingThings.add(gpioButtonClickable);
            return new Clickable() {
                @Override
                public void setListener(final Listener listener) {
                    gpioButtonClickable.setListener(listener);
                    restartButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClick();
                        }
                    });
                }
            };
        } else {
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
        if (isThingsDevice()) {
            PwmPiSpeaker pwmPiSpeaker = new PwmPiSpeaker();
            androidThingThings.add(pwmPiSpeaker);
            return pwmPiSpeaker;
        } else {
            return new AndroidSynthSpeaker();
        }
    }

    private ScoreDisplayer createScoreDisplayer() {
        if (isThingsDevice()) {
            RainbowHatScoreDisplayer rainbowHatScoreDisplayer = new RainbowHatScoreDisplayer();
            androidThingThings.add((rainbowHatScoreDisplayer));
            return rainbowHatScoreDisplayer;
        } else {
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

    private boolean isThingsDevice() {
        // TODO once targeting 'O' use constant `PackageManager.FEATURE_EMBEDDED`
        return getPackageManager().hasSystemFeature("android.hardware.type.embedded");
    }
}
