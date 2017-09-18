package com.novoda.tpbot.landing;

import android.view.View;

class LandingDisplayer {

    private final View humanSelectionView;
    private final View botSelectionView;

    LandingDisplayer(View humanSelectionView, View botSelectionView) {
        this.humanSelectionView = humanSelectionView;
        this.botSelectionView = botSelectionView;
    }

    void update(final LandingPresenter.Listener selectionListener) {
        humanSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.onHumanSelected();
            }
        });

        botSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.onBotSelected();
            }
        });
    }
}
