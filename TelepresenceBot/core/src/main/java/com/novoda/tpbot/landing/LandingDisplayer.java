package com.novoda.tpbot.landing;

class LandingDisplayer {

    private final LandingView landingView;

    LandingDisplayer(LandingView landingView) {
        this.landingView = landingView;
    }

    void update(final LandingPresenter.Listener selectionListener) {
        landingView.setSelectionListener(onLandingSelectionListener(selectionListener));
    }

    private Listener onLandingSelectionListener(final LandingPresenter.Listener selectionListener) {
        return new Listener() {
            @Override
            public void onHumanSelected() {
                selectionListener.onHumanSelected();
            }

            @Override
            public void onBotSelected() {
                selectionListener.onBotSelected();
            }
        };
    }

    public interface Listener {
        void onHumanSelected();

        void onBotSelected();
    }
}
