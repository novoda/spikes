package com.novoda.pianohero;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GameScreen extends LinearLayoutCompat {

    private C4ToB5TrebleStaffWidget trebleStaffWidget;
    private TextView playNoteTextView;
    private TextView nextNoteTextView;
    private TextView statusTextView;

    public GameScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_game_screen, this);
        trebleStaffWidget = (C4ToB5TrebleStaffWidget) findViewById(R.id.game_screen_widget_treble_staff);
        playNoteTextView = (TextView) findViewById(R.id.game_screen_text_play_note);
        nextNoteTextView = (TextView) findViewById(R.id.game_screen_text_next_note);
        statusTextView = (TextView) findViewById(R.id.game_screen_text_status);
    }

    public void showGameStart(GameStartViewModel viewModel) {
        statusTextView.setText(viewModel.getStartMessage());

        trebleStaffWidget.showProgress(viewModel.getSequence());
        trebleStaffWidget.setVisibility(View.VISIBLE);
    }

    public void showSongStart(SongStartViewModel viewModel) {
        playNoteTextView.setText(viewModel.getCurrentNoteFormatted());
        nextNoteTextView.setText(viewModel.getNextNoteFormatted());
    }

    public void showSuccess(RoundEndViewModel viewModel) {
        statusTextView.setText(viewModel.getSuccessMessage());

        playNoteTextView.setText(viewModel.getCurrentNoteFormatted());
        nextNoteTextView.setText(viewModel.getNextNoteFormatted());

        trebleStaffWidget.showProgress(viewModel.getSequence());
    }

    public void showSongComplete(SongCompleteViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
    }

    public void showError(RoundErrorViewModel viewModel) {
        statusTextView.setText(viewModel.getErrorMessage());
        trebleStaffWidget.showError(viewModel);
    }

    public void showSharpError(RoundErrorViewModel viewModel) {
        statusTextView.setText(viewModel.getErrorMessage());
        trebleStaffWidget.showSharpError(viewModel);
    }

    public void showGameComplete(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.setVisibility(View.GONE);
    }

    public void showClock(ClockViewModel viewModel) {
//        statusTextView.setText(viewModel.getTimeLeftFormatted());
        // TODO View stuff @ataulm
        Log.d("!!", viewModel.getTimeLeftFormatted() + " tick.");
    }
}
