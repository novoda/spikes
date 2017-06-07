package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(GameStartCallback callback,
                       GameClockCallback cCallback,
                       RoundCallback rCallback,
                       SongCompleteCallback sCallback,
                       GameCompleteCallback gCallback);

        void startup();


        void shutdown();

        interface GameStartCallback {
            void onGameStarted(RoundEndViewModel viewModel);
        }

        interface GameClockCallback {
            void onClockTick(ClockViewModel viewModel);
        }

        interface RoundCallback {
            void onRoundStart(RoundStartViewModel viewModel);

            void onRoundEnd(RoundEndViewModel viewModel);

            void onRoundSuccess(RoundSuccessViewModel viewModel);

            void onRoundError(RoundErrorViewModel viewModel);
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

        void showError(RoundErrorViewModel viewModel);

        void showSharpError(RoundErrorViewModel viewModel);

        void showScore(String score);
    }

    interface Presenter {

        void onCreate();

        void onResume();

        void onPause();
    }

}
