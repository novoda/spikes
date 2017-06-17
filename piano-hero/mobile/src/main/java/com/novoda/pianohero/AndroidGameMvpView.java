package com.novoda.pianohero;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

class AndroidGameMvpView implements GameMvp.View {

    private final Speaker speaker;
    private final ScoreDisplayer scoreDisplayer;
    private final C4ToB5TrebleStaffWidget trebleStaffWidget;
    private final TextView playNoteTextView;
    private final TextView nextNoteTextView;
    private final TextView statusTextView;

    AndroidGameMvpView(Speaker speaker, ScoreDisplayer scoreDisplayer, C4ToB5TrebleStaffWidget trebleStaffWidget, TextView playNoteTextView, TextView nextNoteTextView, TextView statusTextView) {
        this.speaker = speaker;
        this.scoreDisplayer = scoreDisplayer;
        this.trebleStaffWidget = trebleStaffWidget;
        this.playNoteTextView = playNoteTextView;
        this.nextNoteTextView = nextNoteTextView;
        this.statusTextView = statusTextView;
    }

    @Override
    public void play(Sound sound) {
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
    public void showClock(ClockViewModel viewModel) {
        Log.d("!!", viewModel.getTimeLeftFormatted() + " tick.");
    }

    @Override
    public void show(GameInProgressViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.showProgress(viewModel.getSequence());
        trebleStaffWidget.setVisibility(View.VISIBLE);
        playNoteTextView.setText(viewModel.getCurrentNote());
        nextNoteTextView.setText(viewModel.getUpcomingNote());
        scoreDisplayer.display(viewModel.getScore());
    }

    @Override
    public void showSongComplete(SongCompleteViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.setVisibility(View.GONE);
        scoreDisplayer.clearScore();
    }
}
