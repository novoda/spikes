package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(GameCallback gameCallback);

        void stopGame();

        interface GameCallback {

            void onSongStarted(SongStartViewModel viewModel);

            void onSongComplete();

            void onClockTick(ClockViewModel viewModel);

            void onGameProgressing(GameInProgressViewModel viewModel);

            void onGameComplete(GameOverViewModel viewModel);
        }
    }

    interface View {

        void play(Sound sound);

        void show(GameInProgressViewModel viewModel);

        void showClock(ClockViewModel viewModel);

        void showGameStarted(GameStartViewModel viewModel);

        void showSong(SongStartViewModel viewModel);

        void showSongComplete(SongCompleteViewModel viewModel);

        void showGameComplete(GameOverViewModel viewModel);
    }

    interface Presenter {

        void startPresenting();

        void stopPresenting();
    }
}
