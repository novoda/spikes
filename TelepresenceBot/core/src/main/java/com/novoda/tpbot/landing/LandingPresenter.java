package com.novoda.tpbot.landing;

class LandingPresenter {

    private final LandingDisplayer displayer;
    private final Navigator navigator;

    LandingPresenter(LandingDisplayer displayer, Navigator navigator) {
        this.displayer = displayer;
        this.navigator = navigator;
    }

    void startPresenting() {
        displayer.update(selectionListener);
    }

    void stopPresenting() {

    }

    private final Listener selectionListener = new Listener() {
        @Override
        public void onBotSelected() {
            navigator.toBot();
        }

        @Override
        public void onHumanSelected() {
            navigator.toHuman();
        }
    };

    interface Listener {

        void onBotSelected();

        void onHumanSelected();

    }

}
