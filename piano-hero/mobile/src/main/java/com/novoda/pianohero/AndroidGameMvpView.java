package com.novoda.pianohero;

import android.view.View;
import android.widget.TextView;

class AndroidGameMvpView implements GameMvp.View {

    private final Speaker speaker;
    private final ScoreDisplayer scoreDisplayer;
    private final C4ToB5TrebleStaffWidget trebleStaffWidget;
    private final View trebleStaffContainerView;
    private final TextView playNoteTextView;
    private final TextView nextNoteTextView;
    private final TextView statusTextView;
    private final TimerWidget timerWidget;

    AndroidGameMvpView(Speaker speaker, ScoreDisplayer scoreDisplayer, C4ToB5TrebleStaffWidget trebleStaffWidget, View trebleStaffContainerView, TextView playNoteTextView, TextView nextNoteTextView, TextView statusTextView, TimerWidget timerWidget) {
        this.speaker = speaker;
        this.scoreDisplayer = scoreDisplayer;
        this.trebleStaffWidget = trebleStaffWidget;
        this.trebleStaffContainerView = trebleStaffContainerView;
        this.playNoteTextView = playNoteTextView;
        this.nextNoteTextView = nextNoteTextView;
        this.statusTextView = statusTextView;
        this.timerWidget = timerWidget;
    }

    @Override
    public void show(GameInProgressViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.showProgress(viewModel.getSequence());
        playNoteTextView.setText(viewModel.getCurrentNote());
        nextNoteTextView.setText(viewModel.getUpcomingNote());
        scoreDisplayer.display(viewModel.getScore());
        timerWidget.setText(viewModel.getTimeRemaining().getRemainingText());
        timerWidget.setProgress(viewModel.getTimeRemaining().getProgress());

        trebleStaffContainerView.setVisibility(View.VISIBLE);
        timerWidget.setVisibility(View.VISIBLE);
        playNoteTextView.setVisibility(View.VISIBLE);
        nextNoteTextView.setVisibility(View.VISIBLE);

        play(viewModel.getSound());
    }

    private void play(Sound sound) {
        if (sound.isOfSilence()) {
            stopSound();
        } else {
            startSound(sound.frequency());
        }
    }

    private void stopSound() {
        speaker.stop();
    }

    private void startSound(double midi) {
        speaker.stop();
        speaker.start(midi);
    }

    @Override
    public void show(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());

        playNoteTextView.setVisibility(View.GONE);
        nextNoteTextView.setVisibility(View.GONE);
        trebleStaffContainerView.setVisibility(View.GONE);
        timerWidget.setVisibility(View.GONE);
        scoreDisplayer.hide();
        stopSound();
    }
}
