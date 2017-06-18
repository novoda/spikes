package com.novoda.pianohero;

class GamePresenter implements GameMvp.Presenter {

    private final GameMvp.Model model;
    private final GameMvp.View view;

    GamePresenter(GameMvp.Model model, GameMvp.View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void startPresenting() {
        model.startGame(gameCallback);
    }

    private final GameMvp.Model.GameCallback gameCallback = new GameMvp.Model.GameCallback() {
        @Override
        public void onGameProgressing(GameInProgressViewModel viewModel) {
            view.show(viewModel);
        }

        @Override
        public void onGameComplete(GameOverViewModel viewModel) {
            view.show(viewModel);
        }
    };

    @Override
    public void stopPresenting() {
        model.stopGame();
    }
}
