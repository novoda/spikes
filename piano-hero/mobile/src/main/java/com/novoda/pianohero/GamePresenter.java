package com.novoda.pianohero;

class GamePresenter implements GameMvp.Presenter {

    private final GameMvp.Model gameModel;
    private final GameMvp.View view;

    GamePresenter(GameMvp.Model gameModel, GameMvp.View view) {
        this.gameModel = gameModel;
        this.view = view;
    }

    @Override
    public void onCreate() {
        gameModel.startGame(
            gameStartCallback,
            gameClockCallback,
            roundCallback,
            songCompleteCallback,
            gameCompleteCallback
        );
    }

    @Override
    public void onResume() {
        gameModel.startup();
    }

    private final GameMvp.Model.GameStartCallback gameStartCallback = new GameMvp.Model.GameStartCallback() {
        @Override
        public void onGameStarted(GameStartViewModel viewModel) {
            view.showGameStarted(viewModel);
        }
    };

    private final GameMvp.Model.GameClockCallback gameClockCallback = new GameMvp.Model.GameClockCallback() {
        @Override
        public void onClockTick(ClockViewModel viewModel) {
            view.showClock(viewModel);
        }
    };

    private final GameMvp.Model.RoundCallback roundCallback = new GameMvp.Model.RoundCallback() {
        @Override
        public void onRoundStart(RoundStartViewModel viewModel) {
            view.startSound(viewModel.getNoteFrequency());
        }

        @Override
        public void onRoundEnd(RoundEndViewModel viewModel) {
            view.stopSound();
            view.showRound(viewModel);
        }

        @Override
        public void onRoundSuccess(RoundSuccessViewModel viewModel) {
            view.showScore(viewModel.getScoreFormatted());
        }

        @Override
        public void onRoundError(RoundErrorViewModel viewModel) {
            view.showScore(viewModel.getScoreFormatted());

            if (viewModel.isSharpError()) {
                view.showSharpError(viewModel);
            } else {
                view.showError(viewModel);
            }
        }
    };

    private final GameMvp.Model.SongCompleteCallback songCompleteCallback = new GameMvp.Model.SongCompleteCallback() {
        @Override
        public void onSongComplete() {
            view.showSongComplete(new SongCompleteViewModel("show complete, start another!"));
        }
    };

    private final GameMvp.Model.GameCompleteCallback gameCompleteCallback = new GameMvp.Model.GameCompleteCallback() {
        @Override
        public void onGameComplete(GameOverViewModel viewModel) {
            view.showGameComplete(viewModel);
        }
    };

    @Override
    public void onPause() {
        gameModel.shutdown();
    }

}
