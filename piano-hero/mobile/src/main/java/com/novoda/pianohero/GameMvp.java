package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        RoundViewModel startGame();

        RoundViewModel onNotesPlayed(Callback callback, Note... note);

        interface Callback {
            void onSequenceComplete();
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
