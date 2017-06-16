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
    public void startGame(final SongStartCallback songStartCallback,
                          final GameClockCallback clockCallback,
                          final GameProgressCallback gameProgressCallback,
                          final SongCompleteCallback songCompleteCallback,
                          final GameCompleteCallback gameCompleteCallback) {
        score = START_SCORE;

        startGameClickable.setListener(new Clickable.Listener() {
            @Override
            public void onClick() {
                startGame(
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
                GameInProgressViewModel gameInProgressViewModel = converter.createCurrentlyPressingNoteGameInProgressViewModel(note, sequence, score);
                gameProgressCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onCorrectNotePlayed(Sequence sequence) {
                score = score.add(7);
                GameInProgressViewModel gameInProgressViewModel = converter.createCorrectNotePressedGameInProgressViewModel(sequence, score);
                gameProgressCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onIncorrectNotePlayed(Sequence sequence) {
                score = score.minus(3);
                GameInProgressViewModel gameInProgressViewModel = converter.createIncorrectNotePressedGameInProgressViewModel(sequence, score);
                gameProgressCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onFinalNoteInSequencePlayedSuccessfully() {
                songCompleteCallback.onSongComplete();
                startNextSong();
            }

        });

        piano.attachListener(songPlayingNoteListener);

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
        GameInProgressViewModel viewModel = converter.createStartGameInProgressViewModel(sequence, score);
        gameProgressCallback.onGameProgressing(viewModel);
    }

    private final Piano.NoteListener songPlayingNoteListener = new Piano.NoteListener() {
        @Override
        public void onStart(Note note) {
            if (gameTimer.gameInProgress()) {
                songPlayer.onStartPlaying(note);
            }
        }

        @Override
        public void onStop(Note note) {
            if (gameTimer.gameInProgress()) {
                songPlayer.onStopPlaying(note);
            }
        }
    };

    @Override
    public void stopGame() {
        gameTimer.stop();
    }

    private void startNextSong() {
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        songPlayer.loadSong(sequence);
    }
}
