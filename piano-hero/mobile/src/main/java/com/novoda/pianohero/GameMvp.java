package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(GameStartCallback callback,
                       GameClockCallback cCallback,
                       RoundCallback rCallback,
                       SongCompleteCallback sCallback,
                       GameCompleteCallback gCallback);

        void startup();

        void playGameRound(RoundCallback rCallback, SongCompleteCallback cCallback, Note note);

        void shutdown();

        interface GameStartCallback {
            void onGameStarted(RoundEndViewModel viewModel);
        }

        interface GameClockCallback {
            void onClockTick(ClockViewModel viewModel);
        }

        interface RoundCallback {
            void onRoundStart(RoundStartViewModel viewModel);

            void onRoundUpdate(RoundEndViewModel viewModel);
        }

        interface SongCompleteCallback {
            void onSongComplete();
        }

        interface GameCompleteCallback {
            void onGameComplete(GameOverViewModel viewModel);
        }
    }

    interface View {

        void startSound(double midi);

        void stopSound();

        void showClock(ClockViewModel viewModel);

        void showRound(RoundEndViewModel viewModel);

        void showSongComplete(SongCompleteViewModel viewModel);

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
