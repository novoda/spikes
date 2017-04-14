package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(StartCallback callback);

        void playGameRound(RoundCallback rCallback, CompletionCallback cCallback, Note... notes);

        interface StartCallback {
            void onGameStarted(RoundViewModel viewModel);
        }

        interface RoundCallback {
            void onRoundUpdate(RoundViewModel viewModel);
        }

        interface CompletionCallback {
            void onGameComplete();
        }
    }

    interface View {

        void showRound(RoundViewModel viewModel);

        void showGameComplete(GameOverViewModel viewModel);
    }

    interface Presenter {

        void onCreate();

        void onNotesPlayed(Note... notes);

        void onRestartGameSelected();
    }

}
