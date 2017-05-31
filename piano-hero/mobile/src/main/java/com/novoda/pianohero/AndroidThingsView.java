package com.novoda.pianohero;

import android.widget.TextView;

import java.util.Collections;

class AndroidThingsView implements GameMvp.View {

    private final TextView statusTextView;
    private final C4ToB5TrebleStaffWidget c4ToB5TrebleStaffWidget;

    AndroidThingsView(TextView statusTextView, C4ToB5TrebleStaffWidget c4ToB5TrebleStaffWidget) {
        this.statusTextView = statusTextView;
        this.c4ToB5TrebleStaffWidget = c4ToB5TrebleStaffWidget;
    }

    @Override
    public void showRound(RoundViewModel viewModel) {
        Sequence sequence = viewModel.getSequence();
        if (sequence.latestError() != null && betweenC4AndB5Inclusive(sequence.latestError())) {
            c4ToB5TrebleStaffWidget.show(sequence.notes().asList(), sequence.position(), sequence.latestError());
        } else {
            c4ToB5TrebleStaffWidget.show(sequence.notes().asList(), sequence.position());
        }
        statusTextView.setText(viewModel.getStatusMessage());
    }

    private boolean betweenC4AndB5Inclusive(Note note) {
        return note.midi() >= 60 && note.midi() <= 83;
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        c4ToB5TrebleStaffWidget.show(Collections.<Note>emptyList(), 0);
    }
}
