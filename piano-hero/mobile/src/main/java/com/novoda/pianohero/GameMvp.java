package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(StartCallback callback, RoundCallback rCallback, SongCompleteCallback cCallback);

        void startup();

        void playGameRound(RoundCallback rCallback, SongCompleteCallback cCallback, Note note);

        void shutdown();

        interface StartCallback {
            void onGameStarted(RoundEndViewModel viewModel);
        }

        interface RoundCallback {
            void onRoundStart(RoundStartViewModel viewModel);

            void onRoundUpdate(RoundEndViewModel viewModel);
        }

        interface SongCompleteCallback {
            void onSongComplete();
        }
    }

    interface View {

        void startSound(double midi);

        void stopSound();

        void showRound(RoundEndViewModel viewModel);

        void showGameComplete(GameOverViewModel viewModel);

        void showError(RoundEndViewModel viewModel);

        void showSharpError(RoundEndViewModel viewModel);
    }

    interface Presenter {

        void onCreate();

        void onResume();

        void onPause();
    }

}
