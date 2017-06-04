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
        gameModel.startGame(andInformView, roundCallback, gameCompletionCallback);
    }

    @Override
    public void onResume() {
        gameModel.startup();
    }

    private final GameMvp.Model.StartCallback andInformView = new GameMvp.Model.StartCallback() {
        @Override
        public void onGameStarted(RoundViewModel viewModel) {
            view.showRound(viewModel);
        }
    };

    private final GameMvp.Model.RoundCallback roundCallback = new GameMvp.Model.RoundCallback() {
        @Override
        public void onRoundUpdate(RoundViewModel viewModel) {
            view.showRound(viewModel);
            if (viewModel.hasError()) {
                view.showError(viewModel);
            }
        }
    };

    private final GameMvp.Model.CompletionCallback gameCompletionCallback = new GameMvp.Model.CompletionCallback() {
        @Override
        public void onGameComplete() {
            view.showGameComplete(new GameOverViewModel("game complete, another!"));
            gameModel.startGame(andInformView, roundCallback, gameCompletionCallback);
        }
    };

    @Override
    public void onPause() {
        gameModel.shutdown();
    }

}
