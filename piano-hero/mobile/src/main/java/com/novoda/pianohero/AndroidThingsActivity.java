package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import static android.view.View.GONE;

public class AndroidThingsActivity extends AppCompatActivity implements GameMvp.View {

    private GamePresenter gamePresenter;
    private GameScreen gameScreen;
    private Speaker speaker;
    private TouchButton touchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        speaker = new Speaker(getPackageManager());
        speaker.open();
        touchButton = new TouchButton();
        touchButton.open();
        SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();
        Piano piano = createPiano();
        SongSequenceFactory songSequenceFactory = new SongSequenceFactory();
        ViewModelConverter viewModelConverter = new ViewModelConverter(simplePitchNotationFormatter);
        SongPlayer songPlayer = new SongPlayer();
        GameModel gameModel = new GameModel(songSequenceFactory, piano, touchButton, viewModelConverter, songPlayer);
        gameScreen = (GameScreen) findViewById(R.id.game_screen);
        gamePresenter = new GamePresenter(gameModel, this);

        gamePresenter.onCreate();
    }

    private Piano createPiano() {
        C4ToB5ViewPiano virtualPianoView = (C4ToB5ViewPiano) findViewById(R.id.piano_view);
        if (isThingsDevice()) {
            virtualPianoView.setVisibility(GONE);
            return new CompositePiano(new KeyStationMini32Piano(this));
        } else {
            virtualPianoView.setVisibility(View.VISIBLE);
            return new CompositePiano(virtualPianoView, new KeyStationMini32Piano(this));
        }
    }

    private boolean isThingsDevice() {
        return getPackageManager().hasSystemFeature("android.hardware.type.embedded");
        // TODO once targeting 'O' use constant
        // PackageManager.FEATURE_EMBEDDED
    }

    @Override
    protected void onResume() {
        super.onResume();
        gamePresenter.onResume();
    }

    @Override
    public void startSound(double midi) {
        speaker.start(midi);
    }

    @Override
    public void stopSound() {
        speaker.stop();
    }

    @Override
    public void showClock(ClockViewModel viewModel) {
        gameScreen.showClock(viewModel);
    }

    @Override
    public void showGameStarted(GameStartViewModel viewModel) {
        gameScreen.showGameStart(viewModel);
    }

    @Override
    public void showSong(SongStartViewModel viewModel) {
        gameScreen.showSongStart(viewModel);
    }

    @Override
    public void showRound(RoundEndViewModel viewModel) {
        gameScreen.showSuccess(viewModel);
    }

    @Override
    public void showSongComplete(SongCompleteViewModel viewModel) {
        gameScreen.showSongComplete(viewModel);
    }

    @Override
    public void showError(RoundErrorViewModel viewModel) {
        gameScreen.showError(viewModel);
    }

    @Override
    public void showSharpError(RoundErrorViewModel viewModel) {
        gameScreen.showSharpError(viewModel);
    }

    @Override
    public void showScore(String score) {
        Log.d("!!", "Update " + score);
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        gameScreen.showGameComplete(viewModel);
    }

    @Override
    protected void onPause() {
        gamePresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        touchButton.close();
        speaker.close();
        super.onDestroy();
    }
}
