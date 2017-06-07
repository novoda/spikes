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
        gameModel.startGame(andInformView, roundCallback, songCompleteCallback);
    }

    @Override
    public void onResume() {
        gameModel.startup();
    }

    private final GameMvp.Model.StartCallback andInformView = new GameMvp.Model.StartCallback() {
        @Override
        public void onGameStarted(RoundEndViewModel viewModel) {
            view.showRound(viewModel);
        }
    };

    private final GameMvp.Model.RoundCallback roundCallback = new GameMvp.Model.RoundCallback() {
        @Override
        public void onRoundStart(RoundStartViewModel viewModel) {
            view.startSound(viewModel.getNoteFrequency());
        }

        @Override
        public void onRoundUpdate(RoundEndViewModel viewModel) {
            view.stopSound();
            view.showRound(viewModel);
            if (viewModel.hasError()) {
                if (viewModel.isSharpError()) {
                    view.showSharpError(viewModel);
                } else {
                    view.showError(viewModel);
                }
            }
        }
    };

    private final GameMvp.Model.SongCompleteCallback songCompleteCallback = new GameMvp.Model.SongCompleteCallback() {
        @Override
        public void onGameComplete() {
            view.showGameComplete(new GameOverViewModel("game complete, another!"));
            gameModel.startGame(andInformView, roundCallback, songCompleteCallback);
        }
    };

    @Override
    public void onPause() {
        gameModel.shutdown();
    }

}
