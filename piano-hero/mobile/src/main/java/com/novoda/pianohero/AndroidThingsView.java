package com.novoda.pianohero;

import android.view.View;
import android.widget.TextView;

class AndroidThingsView implements GameMvp.View {

    private final TextView statusTextView;
    private final C4ToB5TrebleStaffWidget c4ToB5TrebleStaffWidget;

    AndroidThingsView(TextView statusTextView, C4ToB5TrebleStaffWidget c4ToB5TrebleStaffWidget) {
        this.statusTextView = statusTextView;
        this.c4ToB5TrebleStaffWidget = c4ToB5TrebleStaffWidget;
    }

    @Override
    public void showRound(RoundViewModel viewModel) {
        statusTextView.setText(viewModel.getStatusMessage());
        c4ToB5TrebleStaffWidget.show(viewModel.getSequence());
        c4ToB5TrebleStaffWidget.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        c4ToB5TrebleStaffWidget.setVisibility(View.GONE);
    }
}
