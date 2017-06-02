package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(StartCallback callback, RoundCallback rCallback, CompletionCallback cCallback);

        void startup();

        void playGameRound(RoundCallback rCallback, CompletionCallback cCallback, Note note);

        void shutdown();

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

        void onResume();

        void onPause();
    }

}
