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
        RoundViewModel viewModel = gameModel.startGame();
        view.showRound(viewModel);
    }

    @Override
    public void onNotesPlayed(Note... notes) {
        RoundViewModel viewModel = gameModel.onNotesPlayed(new GameMvp.Model.Callback() {
            @Override
            public void onSequenceComplete() {
                view.showGameComplete();
                gameModel.startGame();
            }
        }, notes);
        if (viewModel == null) {
            return;
        }
        view.showRound(viewModel);
    }

    @Override
    public void onRestartGameSelected() {
        RoundViewModel viewModel = gameModel.startGame();
        view.showRound(viewModel);
    }

    @Override
    public void onShowKeyboardSelected() {
        view.showKeyboard();
    }
}
