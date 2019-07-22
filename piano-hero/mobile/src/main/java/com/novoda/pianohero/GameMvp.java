package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        void startGame(GameCallback gameCallback);

        void stopGame();

        interface GameCallback {

            void onGameProgressing(GameInProgressViewModel viewModel);

            void onGameComplete(GameOverViewModel viewModel);
        }
    }

    interface View {

        void show(GameInProgressViewModel viewModel);

        void show(GameOverViewModel viewModel);
    }

    interface Presenter {

        void startPresenting();

        void stopPresenting();
    }
}
