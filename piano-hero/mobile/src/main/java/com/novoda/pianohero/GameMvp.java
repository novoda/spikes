package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(GameCallback gameCallback);

        void stopGame();

        interface GameCallback {

            void onSongComplete();

            void onGameProgressing(GameInProgressViewModel viewModel);

            void onGameComplete(GameOverViewModel viewModel);
        }
    }

    interface View {

        void play(Sound sound);

        void show(GameInProgressViewModel viewModel);

        void showSongComplete(SongCompleteViewModel viewModel);

        void showGameComplete(GameOverViewModel viewModel);
    }

    interface Presenter {

        void startPresenting();

        void stopPresenting();
    }
}
