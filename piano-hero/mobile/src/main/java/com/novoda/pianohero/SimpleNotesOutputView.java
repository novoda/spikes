package com.novoda.pianohero;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleNotesOutputView extends LinearLayout implements GameMvp.View {

    private TextView nextNoteToPlayTextView;
    private TextView messageTextView;

    public SimpleNotesOutputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_simple_notes_output, this);
        nextNoteToPlayTextView = (TextView) findViewById(R.id.simple_notes_output_text_next_note);
        messageTextView = (TextView) findViewById(R.id.simple_notes_output_text_message);
    }

    @Override
    public void showRound(RoundViewModel viewModel) {
        updateNextNoteToPlay(viewModel);
        messageTextView.setText(viewModel.getStatusMessage());
    }

    private void updateNextNoteToPlay(RoundViewModel viewModel) {
        for (String note : viewModel) {
            nextNoteToPlayTextView.setText(note);
        }
    }

    @Override
    public void showGameComplete() {
        PianoHeroApplication.popToast("game complete, another!");
    }

    @Override
    public void showError() {
        PianoHeroApplication.popToast("that's not a simple note!");
    }

}
