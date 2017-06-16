package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static android.view.View.GONE;

public class AndroidThingsActivity extends AppCompatActivity {

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
        return new GameModel(
                new SongSequenceFactory(),
                createPiano(),
                createRestartGameClickable(),
                new GameTimer(),
                new ViewModelConverter(new SimplePitchNotationFormatter()),
                new SongPlayer()
        );
    }

    private Piano createPiano() {
        C4ToB5ViewPiano virtualPianoView = (C4ToB5ViewPiano) findViewById(R.id.piano_view);
        KeyStationMini32Piano keyStationMini32Piano = new KeyStationMini32Piano(this);
        androidThingThings.add(keyStationMini32Piano);

        if (isThingsDevice()) {
            virtualPianoView.setVisibility(GONE);
            return keyStationMini32Piano;
        } else {
            virtualPianoView.setVisibility(View.VISIBLE);
            return new CompositePiano(virtualPianoView, keyStationMini32Piano);
        }
    }

    private Clickable createRestartGameClickable() {
        if (isThingsDevice()) {
            GpioButtonClickable gpioButtonClickable = new GpioButtonClickable();
            androidThingThings.add(gpioButtonClickable);
            return gpioButtonClickable;
        } else {
            final View restartButton = findViewById(R.id.restart_button);
            restartButton.setVisibility(View.VISIBLE);
            return new Clickable() {
                @Override
                public void setListener(final Listener listener) {
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
                createScoreDisplayer(),
                (C4ToB5TrebleStaffWidget) findViewById(R.id.game_widget_treble_staff),
                (TextView) findViewById(R.id.game_text_play_note),
                (TextView) findViewById(R.id.game_text_next_note),
                (TextView) findViewById(R.id.game_text_status)
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
                }

                @Override
                public void clearScore() {
                    scoreTextView.setText(null);
                }
            };
        }
    }

    private boolean isThingsDevice() {
        // TODO once targeting 'O' use constant `PackageManager.FEATURE_EMBEDDED`
        return getPackageManager().hasSystemFeature("android.hardware.type.embedded");
    }
}
