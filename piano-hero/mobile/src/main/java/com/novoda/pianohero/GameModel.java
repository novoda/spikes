package com.novoda.pianohero;

public class GameModel implements GameMvp.Model {

    private static final Score START_SCORE = new Score(500);

    private final SongSequenceFactory songSequenceFactory;
    private final Piano piano;
    private final SongPlayer songPlayer;
    private final Clickable startGameClickable;
    private final ViewModelConverter converter;
    private final GameTimer gameTimer;

    private Score score = START_SCORE;

    GameModel(
            SongSequenceFactory songSequenceFactory,
            Piano piano,
            Clickable startGameClickable,
            GameTimer gameTimer,
            ViewModelConverter converter,
            SongPlayer songPlayer
    ) {
        this.songSequenceFactory = songSequenceFactory;
        this.piano = piano;
        this.startGameClickable = startGameClickable;
        this.gameTimer = gameTimer;
        this.converter = converter;
        this.songPlayer = songPlayer;
    }

    @Override
    public void startGame(GameCallback gameCallback) {
//        sendInitialGameState(gameCallback); TODO this should be like initial sequence with initial score
    }

    @Override
    public void startGame(final GameStartCallback callback,
                          final SongStartCallback songStartCallback,
                          final GameClockCallback clockCallback,
                          final GameProgressCallback gameProgressCallback,
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
                        gameProgressCallback,
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
            public void onStartPlayingNote(Note note, Sequence sequence) {
                if (gameTimer.gameInProgress()) {
                    GameInProgressViewModel gameInProgressViewModel = converter.createCurrentlyPressingNoteGameInProgressViewModel(note, sequence, score);
                    gameProgressCallback.onGameProgressing(gameInProgressViewModel);
                }
            }

            @Override
            public void onCorrectNotePlayed(Sequence sequence) {
                if (gameTimer.gameInProgress()) {
                    score = score.add(7);
                    GameInProgressViewModel gameInProgressViewModel = converter.createCorrectNotePressedGameInProgressViewModel(sequence, score);
                    gameProgressCallback.onGameProgressing(gameInProgressViewModel);
                }
            }

            @Override
            public void onIncorrectNotePlayed(Sequence sequence) {
                if (gameTimer.gameInProgress()) {
                    score = score.minus(3);
                    GameInProgressViewModel gameInProgressViewModel = converter.createIncorrectNotePressedGameInProgressViewModel(sequence, score);
                    gameProgressCallback.onGameProgressing(gameInProgressViewModel);
                }
            }

            @Override
            public void onFinalNoteInSequencePlayedSuccessfully() {
                songCompleteCallback.onSongComplete();
                startNextSong();
            }

        });

        piano.attachListener(new Piano.NoteListener() {
            @Override
            public void onStart(Note note) {
                songPlayer.onStartPlaying(note);
            }

            @Override
            public void onStop(Note note) {
                songPlayer.onStopPlaying(note);
            }
        });

        gameTimer.start(new GameTimer.Callback() {
            @Override
            public void onSecondTick(long secondsUntilFinished) {
                ClockViewModel clockViewModel = converter.createClockViewModel(secondsUntilFinished);
                clockCallback.onClockTick(clockViewModel);
            }

            @Override
            public void onFinish() {
                gameCompleteCallback.onGameComplete(converter.createGameOverViewModel(score));
            }
        });
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        songPlayer.loadSong(sequence);
        GameStartViewModel viewModel = converter.createGameStartViewModel(sequence);
        callback.onGameStarted(viewModel);
    }

    @Override
    public void stopGame() {
        gameTimer.stop();
    }

    private void startNextSong() {
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        songPlayer.loadSong(sequence);
    }
}
