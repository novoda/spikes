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
        gameModel.startGame(andInformView);
    }

    private final GameMvp.Model.StartCallback andInformView = new GameMvp.Model.StartCallback() {
        @Override
        public void onGameStarted(RoundViewModel viewModel) {
            view.showRound(viewModel);
        }
    };

    @Override
    public void onNotesPlayed(Note... notes) {
        RoundViewModel viewModel = gameModel.onNotesPlayed(new GameMvp.Model.CompletionCallback() {
            @Override
            public void onGameComplete() {
                view.showGameComplete();
                gameModel.startGame(andInformView);
            }
        }, notes);
        if (viewModel == null) {
            return;
        }
        view.showRound(viewModel);
    }

    @Override
    public void onRestartGameSelected() {
        gameModel.startGame(andInformView);
    }

}
