package com.novoda.pianohero;

import android.widget.TextView;

import java.util.Collections;

class AndroidThingsView implements GameMvp.View {

    private final TextView statusTextView;
    private final TrebleStaffWidget trebleStaffWidget;

    AndroidThingsView(TextView statusTextView, TrebleStaffWidget trebleStaffWidget) {
        this.statusTextView = statusTextView;
        this.trebleStaffWidget = trebleStaffWidget;
    }

    @Override
    public void showRound(RoundViewModel viewModel) {
        Sequence sequence = viewModel.getSequence();
        if (sequence.latestError() == null) {
            trebleStaffWidget.show(sequence.notes().notes(), sequence.position());
        } else {
            trebleStaffWidget.show(sequence.notes().notes(), sequence.position(), sequence.latestError());
        }

        statusTextView.setText(viewModel.getStatusMessage());
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.show(Collections.<Note>emptyList(), 0);
    }
}
