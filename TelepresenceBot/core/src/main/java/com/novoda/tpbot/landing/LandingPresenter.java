package com.novoda.tpbot.landing;

class LandingPresenter {

    private final LandingView landingView;
    private final Navigator navigator;

    LandingPresenter(LandingView landingView, Navigator navigator) {
        this.landingView = landingView;
        this.navigator = navigator;
    }

    void startPresenting() {
        landingView.update(actions);
    }

    void stopPresenting() {

    }

    private final Actions actions = new Actions() {
        @Override
        public void onBotSelected() {
            navigator.toBot();
        }

        @Override
        public void onHumanSelected() {
            navigator.toHuman();
        }
    };

    interface Actions {

        void onBotSelected();

        void onHumanSelected();

    }

}
