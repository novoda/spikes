package com.novoda.pianohero;

import java.util.Locale;

public class GameModel implements GameMvp.Model {

    private final SongSequenceFactory songSequenceFactory;
    private final Piano piano;
    private final PlayAttemptGrader playAttemptGrader;
    private final Clickable startGameClickable;
    private final ViewModelConverter converter;
    private final GameTimer gameTimer;
    private final SimplePitchNotationFormatter pitchNotationFormatter;

    private State gameState = State.empty();
    private GameCallback gameCallback;

    GameModel(
            SongSequenceFactory songSequenceFactory,
            Piano piano,
            Clickable startGameClickable,
            GameTimer gameTimer,
            ViewModelConverter converter,
            PlayAttemptGrader playAttemptGrader,
            SimplePitchNotationFormatter pitchNotationFormatter
    ) {
        this.songSequenceFactory = songSequenceFactory;
        this.piano = piano;
        this.startGameClickable = startGameClickable;
        this.gameTimer = gameTimer;
        this.converter = converter;
        this.playAttemptGrader = playAttemptGrader;
        this.pitchNotationFormatter = pitchNotationFormatter;
    }

    @Override
    public void startGame(final GameCallback gameCallback) {
        this.gameCallback = gameCallback;
        startGameClickable.setListener(new Clickable.Listener() {
            @Override
            public void onClick() {
                startNewGame();
            }
        });

        piano.attachListener(songPlayingNoteListener);
        startNewGame();
    }

    private final Piano.NoteListener songPlayingNoteListener = new Piano.NoteListener() {

        @Override
        public void onStart(Note note) {
            if (gameTimer.gameInProgress()) {
                gameState = gameState.update(Sound.of(note))
                        .update(gameTimer.secondsRemaining())
                        .update(Message.empty());

                GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }
        }

        @Override
        public void onStop(Note note) {
            if (gameTimer.gameInProgress()) {
                playAttemptGrader.grade(note, gameState.getSequence(), onPlayAttemptGradedCallback);
            }
        }

        private final PlayAttemptGrader.Callback onPlayAttemptGradedCallback = new PlayAttemptGrader.Callback() {
            @Override
            public void onCorrectNotePlayed(Sequence sequence) {
                gameState = gameState.update(gameState.getScore().increment())
                        .update(sequence)
                        .update(Sound.ofSilence())
                        .update(gameTimer.secondsRemaining())
                        .update(getSuccessMessage(sequence));

                GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            private Message getSuccessMessage(Sequence sequence) {
                if (sequence.position() > 0) {
                    return new Message(String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length()));
                } else {
                    return Message.empty();
                }
            }

            @Override
            public void onIncorrectNotePlayed(Sequence sequence) {
                gameState = gameState.update(gameState.getScore().decrement())
                        .update(sequence)
                        .update(Sound.ofSilence())
                        .update(gameTimer.secondsRemaining())
                        .update(getErrorMessage(sequence));

                GameInProgressViewModel gameInProgressViewModel = converter.createGameInProgressViewModel(gameState);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            private Message getErrorMessage(Sequence sequence) {
                String currentNoteAsText = currentNoteFormatted(sequence.getNextNote());
                String latestErrorAsText = pitchNotationFormatter.format(sequence.latestError());
                return new Message(String.format(Locale.US, "Ruhroh! The correct note is %s but you played %s.", currentNoteAsText, latestErrorAsText));
            }

            private String currentNoteFormatted(Note note) {
                return pitchNotationFormatter.format(note);
            }

            @Override
            public void onFinalNoteInSequencePlayedSuccessfully() {
                gameCallback.onSongComplete();

                Sequence sequence = songSequenceFactory.maryHadALittleLamb(); // TODO: pick next song in playlist
                gameState = gameState.update(sequence)
                        .update(Sound.ofSilence())
                        .update(new Message("nice! next song!"));

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
        public void onSecondTick(long secondsUntilFinished) {
            gameState = gameState.update(secondsUntilFinished);

            GameInProgressViewModel viewModel = converter.createGameInProgressViewModel(gameState);
            gameCallback.onGameProgressing(viewModel);
        }

        @Override
        public void onFinish() {
            gameCallback.onGameComplete(converter.createGameOverViewModel(gameState));
        }
    };

    private void emitInitialGameState(GameCallback gameCallback) {
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        this.gameState = State.empty().update(sequence);

        GameInProgressViewModel viewModel = converter.createGameInProgressViewModel(gameState);
        gameCallback.onGameProgressing(viewModel);
    }

    @Override
    public void stopGame() {
        gameTimer.stop();
    }

}
