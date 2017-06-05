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
            void onRoundStart(int midi);

            void onRoundUpdate(RoundViewModel viewModel);
        }

        interface CompletionCallback {
            void onGameComplete();
        }
    }

    interface View {

        void startSound(int midi);

        void stopSound();

        void showRound(RoundViewModel viewModel);

        void showGameComplete(GameOverViewModel viewModel);

        void showError(RoundViewModel viewModel);
    }

    interface Presenter {

        void onCreate();

        void onResume();

        void onPause();
    }

}
