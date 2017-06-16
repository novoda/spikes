package com.novoda.pianohero;

class GamePresenter implements GameMvp.Presenter {

    private final GameMvp.Model gameModel;
    private final GameMvp.View view;

    GamePresenter(GameMvp.Model gameModel, GameMvp.View view) {
        this.gameModel = gameModel;
        this.view = view;
    }

    @Override
    public void startPresenting() {
        gameModel.startGame(
                gameStartCallback,
                songStartedCallback,
                gameClockCallback,
                gameProgressCallback,
                songCompleteCallback,
                gameCompleteCallback
        );
    }

    @Override
    public void stopPresenting() {
        gameModel.stopGame();
    }

    private final GameMvp.Model.GameStartCallback gameStartCallback = new GameMvp.Model.GameStartCallback() {
        @Override
        public void onGameStarted(GameStartViewModel viewModel) {
            view.showGameStarted(viewModel);
        }
    };

    private final GameMvp.Model.SongStartCallback songStartedCallback = new GameMvp.Model.SongStartCallback() {
        @Override
        public void onSongStarted(SongStartViewModel viewModel) {
            view.showSong(viewModel);
        }
    };

    private final GameMvp.Model.GameClockCallback gameClockCallback = new GameMvp.Model.GameClockCallback() {
        @Override
        public void onClockTick(ClockViewModel viewModel) {
            view.showClock(viewModel);
        }
    };

    private final GameMvp.Model.GameProgressCallback gameProgressCallback = new GameMvp.Model.GameProgressCallback() {
        @Override
        public void onGameProgressing(GameInProgressViewModel viewModel) {
            view.play(viewModel.getSound());
            view.show(viewModel);
        }
    };

    private final GameMvp.Model.SongCompleteCallback songCompleteCallback = new GameMvp.Model.SongCompleteCallback() {
        @Override
        public void onSongComplete() {
            view.showSongComplete(new SongCompleteViewModel("Nice, song complete!"));
        }
    };

    private final GameMvp.Model.GameCompleteCallback gameCompleteCallback = new GameMvp.Model.GameCompleteCallback() {
        @Override
        public void onGameComplete(GameOverViewModel viewModel) {
            view.showGameComplete(viewModel);
        }
    };
}
