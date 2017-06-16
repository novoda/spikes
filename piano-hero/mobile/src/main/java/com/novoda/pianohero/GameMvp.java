package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(
                GameStartCallback callback,
                SongStartCallback ssCallback,
                GameClockCallback cCallback,
                GameProgressCallback rCallback,
                SongCompleteCallback sCallback,
                GameCompleteCallback gCallback
        );

        void stopGame();

        interface GameCallback extends GameStartCallback, SongStartCallback, GameClockCallback, GameProgressCallback, SongCompleteCallback, GameCompleteCallback {
        }

        interface GameStartCallback {
            void onGameStarted(GameStartViewModel viewModel);
        }

        interface SongStartCallback {
            void onSongStarted(SongStartViewModel viewModel);
        }

        interface GameClockCallback {
            void onClockTick(ClockViewModel viewModel);
        }

        interface GameProgressCallback {
            void onGameProgressing(GameInProgressViewModel viewModel);
        }

        interface SongCompleteCallback {
            void onSongComplete();
        }

        interface GameCompleteCallback {
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
