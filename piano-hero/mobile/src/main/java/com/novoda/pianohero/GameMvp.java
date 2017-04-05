package com.novoda.pianohero;

interface GameMvp {

    interface Model {

        RoundViewModel startGame();

        RoundViewModel onNotesPlayed(CompletionCallback callback, Note... note);

        interface CompletionCallback {
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
