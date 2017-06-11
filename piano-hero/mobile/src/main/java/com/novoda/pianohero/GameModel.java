package com.novoda.pianohero;

import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

public class GameModel implements GameMvp.Model {

    private static final long GAME_LENGTH = TimeUnit.SECONDS.toMillis(60);
    private static final long CLOCK_UPDATE_RATE = TimeUnit.SECONDS.toMillis(1);
    private static final int START_SCORE = 500;

    private final SongSequenceFactory songSequenceFactory;
    private final Piano piano;
    private final SongPlayer songPlayer;
    private final Clickable startGameClickable;
    private final ViewModelConverter converter;

    private int score = START_SCORE; // TODO object

    GameModel(
            SongSequenceFactory songSequenceFactory,
            Piano piano,
            Clickable startGameClickable,
            ViewModelConverter converter,
            SongPlayer songPlayer
    ) {
        this.songSequenceFactory = songSequenceFactory;
        this.piano = piano;
        this.startGameClickable = startGameClickable;
        this.converter = converter;
        this.songPlayer = songPlayer;
    }

    @Override
    public void startGame(final GameStartCallback callback,
                          final SongStartCallback songStartCallback,
                          final GameClockCallback clockCallback,
                          final RoundCallback roundCallback,
                          final SongCompleteCallback songCompleteCallback,
                          final GameCompleteCallback gameCompleteCallback) {
        score = START_SCORE;

        startGameClickable.setListener(new Clickable.Listener() {
            @Override
            public void onClick() {
                startGame(
                        callback,
                        songStartCallback,
                        clockCallback,
                        roundCallback,
                        songCompleteCallback,
                        gameCompleteCallback
                );
            }
        });

        songPlayer.attachListeners(new SongPlayer.SongGameCallback() {
            @Override
            public void onSongStarted(Sequence sequence) {
                songStartCallback.onSongStarted(converter.createSongStartViewModel(sequence));
            }

            @Override
            public void onRoundStart(Note note) {
                roundCallback.onRoundStart(converter.createRoundStartViewModel(note));
            }

            @Override
            public void onRoundEnd(Sequence sequence) {
                RoundEndViewModel viewModel = converter.createRoundEndViewModel(sequence);
                roundCallback.onRoundEnd(viewModel);
            }

            @Override
            public void onRoundSuccess(Sequence sequence) {
                score += 7;
                roundCallback.onRoundSuccess(converter.createSuccessViewModel(score));
            }

            @Override
            public void onRoundError(Sequence sequence) {
                score -= 3;
                roundCallback.onRoundError(converter.createErrorViewModel(sequence, score));
            }

            @Override
            public void onSongComplete() {
                songCompleteCallback.onSongComplete();
                startNextSong();
            }

        });

        piano.attachListener(new Piano.NoteListener() {
            @Override
            public void onStart(Note note) {
                songPlayer.onStartPlayed(note);
            }

            @Override
            public void onStop(Note note) {
                songPlayer.onStopPlaying(note);
            }
        });

        CountDownTimer countDownTimer = new CountDownTimer(GAME_LENGTH, CLOCK_UPDATE_RATE) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                clockCallback.onClockTick(converter.createClockViewModel(secondsLeft));
            }

            @Override
            public void onFinish() {
                gameCompleteCallback.onGameComplete(converter.createGameOverViewModel(score));
            }
        };
        countDownTimer.start();
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        songPlayer.loadSong(sequence);
        GameStartViewModel viewModel = converter.createGameStartViewModel(sequence);
        callback.onGameStarted(viewModel);
    }

    @Override
    public void startup() {
        piano.open();
    }

    private void startNextSong() {
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        songPlayer.loadSong(sequence);
    }

    @Override
    public void shutdown() {
        piano.close();
    }
}
