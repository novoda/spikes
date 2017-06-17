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
        gameModel.startGame(gameCallback);
    }

    private final GameMvp.Model.GameCallback gameCallback = new GameMvp.Model.GameCallback() {
        @Override
        public void onClockTick(ClockViewModel viewModel) {
            view.showClock(viewModel);
        }

        @Override
        public void onGameComplete(GameOverViewModel viewModel) {
            view.showGameComplete(viewModel);
        }

        @Override
        public void onGameProgressing(GameInProgressViewModel viewModel) {
            view.play(viewModel.getSound());
            view.show(viewModel);
        }

        @Override
        public void onSongComplete() {
            view.showSongComplete(new SongCompleteViewModel("Nice, song complete!"));
        }

        @Override
        public void onSongStarted(SongStartViewModel viewModel) {
            view.showSong(viewModel);
        }
    };

    @Override
    public void stopPresenting() {
        gameModel.stopGame();
    }

}
