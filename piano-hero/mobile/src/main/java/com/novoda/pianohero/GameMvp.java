package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(StartCallback callback);

        RoundViewModel onNotesPlayed(CompletionCallback callback, Note... note);

        interface StartCallback {
            void onGameStarted(RoundViewModel viewModel);
        }

        interface CompletionCallback {
            void onGameComplete();
        }
    }

    interface View {

        void showRound(RoundViewModel viewModel);

        void showGameComplete();
    }

    interface Presenter {

        void onCreate();

        void onNotesPlayed(Note... notes);

        void onRestartGameSelected();
    }

}
