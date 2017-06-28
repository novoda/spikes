package com.novoda.pianohero;

import android.view.View;
import android.widget.TextView;

class AndroidGameMvpView implements GameMvp.View {

    private final Speaker speaker;
    private final C4ToB5ViewPiano hintPianoWidget;
    private final ScoreDisplayer scoreDisplayer;
    private final C4ToB5TrebleStaffWidget trebleStaffWidget;
    private final TextView statusTextView;
    private final TimerWidget timerWidget;

    AndroidGameMvpView(Speaker speaker, C4ToB5ViewPiano hintPianoWidget, ScoreDisplayer scoreDisplayer, C4ToB5TrebleStaffWidget trebleStaffWidget, TextView statusTextView, TimerWidget timerWidget) {
        this.speaker = speaker;
        this.hintPianoWidget = hintPianoWidget;
        this.scoreDisplayer = scoreDisplayer;
        this.trebleStaffWidget = trebleStaffWidget;
        this.statusTextView = statusTextView;
        this.timerWidget = timerWidget;
    }

    @Override
    public void show(GameInProgressViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.showProgress(viewModel.getSequence());
        hintPianoWidget.display(viewModel.getSequence());
        scoreDisplayer.display(viewModel.getScore());
        timerWidget.setText(viewModel.getTimeRemaining().getRemainingText());
        timerWidget.setProgress(viewModel.getTimeRemaining().getProgress());

        trebleStaffWidget.setVisibility(View.VISIBLE);
        hintPianoWidget.setVisibility(View.VISIBLE);
        timerWidget.setVisibility(View.VISIBLE);

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
        trebleStaffWidget.setVisibility(View.GONE);
        hintPianoWidget.setVisibility(View.GONE);
        timerWidget.setVisibility(View.GONE);
        scoreDisplayer.hide();
        stopSound();
    }
}
