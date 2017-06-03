package com.novoda.pianohero;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class GameScreen extends LinearLayoutCompat implements GameMvp.View {

    private static final String NEXT_NOTE_HINT_FORMAT = "Next: %s";

    private final SimplePitchNotationFormatter formatter;

    private C4ToB5TrebleStaffWidget trebleStaffWidget;
    private View mainMenuButton;
    private View restartButton;
    private TextView playNoteTextView;
    private TextView nextNoteTextView;
    private TextView statusTextView;

    public GameScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
        this.formatter = new SimplePitchNotationFormatter();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_game_screen, this);
        trebleStaffWidget = (C4ToB5TrebleStaffWidget) findViewById(R.id.game_screen_widget_treble_staff);
        mainMenuButton = findViewById(R.id.game_screen_button_main_menu);
        restartButton = findViewById(R.id.game_screen_button_restart);
        playNoteTextView = (TextView) findViewById(R.id.game_screen_text_play_note);
        nextNoteTextView = (TextView) findViewById(R.id.game_screen_text_next_note);
        statusTextView = (TextView) findViewById(R.id.game_screen_text_status);
    }

    @Override
    public void showRound(RoundViewModel viewModel) {
        statusTextView.setText(viewModel.getStatusMessage());

        Sequence sequence = viewModel.getSequence();
        playNoteTextView.setText(currentNote(sequence));
        nextNoteTextView.setText(nextNote(sequence));

        trebleStaffWidget.show(sequence);
        trebleStaffWidget.setVisibility(View.VISIBLE);
    }

    private String currentNote(Sequence sequence) {
        Note note = sequence.get(sequence.position());
        return formatter.format(note);
    }

    private String nextNote(Sequence sequence) {
        if (sequence.position() + 1 < sequence.length()) {
            String nextNote = formatter.format(sequence.get(sequence.position() + 1));
            return String.format(Locale.US, NEXT_NOTE_HINT_FORMAT, nextNote);
        } else {
            return "";
        }
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        statusTextView.setText(viewModel.getMessage());
        trebleStaffWidget.setVisibility(View.GONE);
    }
}
