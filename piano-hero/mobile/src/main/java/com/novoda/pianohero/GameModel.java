package com.novoda.pianohero;

import java.util.Locale;

public class GameModel implements GameMvp.Model {

    private final SongSequencePlaylist songSequencePlaylist;
    private final Piano piano;
    private final PlayAttemptGrader playAttemptGrader;
    private final Clickable startGameClickable;
    private final ViewModelConverter converter;
    private final GameTimer gameTimer;
    private final PhrasesIterator phrasesIterator;

    private State gameState = State.empty();
    private GameCallback gameCallback;

    GameModel(
            SongSequencePlaylist songSequencePlaylist,
            Piano piano,
            Clickable startGameClickable,
            GameTimer gameTimer,
            ViewModelConverter converter,
            PlayAttemptGrader playAttemptGrader,
            PhrasesIterator phrasesIterator) {
        this.songSequencePlaylist = songSequencePlaylist;
        this.piano = piano;
        this.startGameClickable = startGameClickable;
        this.gameTimer = gameTimer;
        this.converter = converter;
        this.playAttemptGrader = playAttemptGrader;
        this.phrasesIterator = phrasesIterator;
    }

    @Override
    public void startGame(GameCallback gameCallback) {
        this.gameCallback = gameCallback;

        startGameClickable.setListener(new Clickable.Listener() {
            @Override
            public void onClick() {
                startNewGame();
            }
        });

        piano.attachListener(onNotePlayedListener);
        startNewGame();
    }

    private final Piano.NoteListener onNotePlayedListener = new Piano.NoteListener() {

        @Override
        public void onStart(Note note) {
            if (gameTimer.gameHasEnded()) {
                return;
            }

            gameState = gameState.update(Sound.of(note));
            GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
            gameCallback.onGameProgressing(gameInProgressViewModel);

            playAttemptGrader.grade(note, gameState.getSequence(), onPlayAttemptGradedCallback);
        }

        @Override
        public void onStop(Note note) {
            if (gameTimer.gameHasEnded()) {
                return;
            }

            gameState = gameState.update(Sound.ofSilence());

            GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
            gameCallback.onGameProgressing(gameInProgressViewModel);
        }

        private final PlayAttemptGrader.Callback onPlayAttemptGradedCallback = new PlayAttemptGrader.Callback() {
            @Override
            public void onCorrectNotePlayed(Sequence sequence) {
                gameState = gameState.update(gameState.getScore().increment())
                        .update(sequence)
                        .update(getSuccessMessage(sequence));

                GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            private Message getSuccessMessage(Sequence sequence) {
                if (sequence.position() > 0) {
                    return new Message(phrasesIterator.nextPlatitude());
                } else {
                    return Message.empty();
                }
            }

            @Override
            public void onIncorrectNotePlayed(Sequence sequence) {
                gameState = gameState.update(gameState.getScore().decrement())
                        .update(sequence)
                        .update(new Message("Uh-oh, try again!"));

                GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onFinalNoteInSequencePlayedSuccessfully() {
                Sequence sequence = songSequencePlaylist.nextSong();
                gameState = gameState.update(sequence)
                        .update(gameState.getScore().increment())
                        .update(new Message(String.format(Locale.US, "Next Song! \"%s\"", sequence.title())));

                GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }
        };
    };

    private void startNewGame() {
        if (gameCallback == null) {
            throw new IllegalStateException("how you startin' a new game without calling startGame(GameCallback)");
        }
        emitInitialGameState(gameCallback);
        gameTimer.start(gameTimerCallback);
    }

    private final GameTimer.Callback gameTimerCallback = new GameTimer.Callback() {
        @Override
        public void onSecondTick(long millisUntilFinished) {
            gameState = gameState.update(millisUntilFinished);

            GameInProgressViewModel viewModel = converter.createGameInProgressViewModel(gameState);
            gameCallback.onGameProgressing(viewModel);
        }

        @Override
        public void onFinish() {
            gameCallback.onGameComplete(converter.createGameOverViewModel(gameState));
        }
    };

    private void emitInitialGameState(GameCallback gameCallback) {
        Sequence sequence = songSequencePlaylist.initial();
        gameState = State.initial(sequence).update(new Message(String.format(Locale.US, "Let's start with \"%s\"!", sequence.title())));

        GameInProgressViewModel viewModel = converter.createGameInProgressViewModel(gameState);
        gameCallback.onGameProgressing(viewModel);
    }

    @Override
    public void stopGame() {
        gameTimer.stop();
    }
}
