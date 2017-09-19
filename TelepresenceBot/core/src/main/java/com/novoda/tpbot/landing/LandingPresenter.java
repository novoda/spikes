package com.novoda.tpbot.landing;

class LandingPresenter {

    private final LandingView landingView;
    private final Navigator navigator;

    LandingPresenter(LandingView landingView, Navigator navigator) {
        this.landingView = landingView;
        this.navigator = navigator;
    }

    void startPresenting() {
        landingView.setSelectionListener(selectionListener);
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
