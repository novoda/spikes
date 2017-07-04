package com.example;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.androidskeleton.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerControlsUi extends LinearLayout {

    @BindView(R.id.player_controls_ui_seekbar)
    SeekBar seekBar;

    @BindView(R.id.player_controls_ui_button_skip_to_start)
    Button skipToStartButton;

    @BindView(R.id.player_controls_ui_button_jump_back)
    Button jumpBackButton;

    @BindView(R.id.player_controls_ui_button_play_pause)
    Button playPauseButton;

    @BindView(R.id.player_controls_ui_button_jump_ahead)
    Button jumpAheadButton;

    @BindView(R.id.player_controls_ui_button_skip_to_end)
    Button skipToEndButton;

    @BindView(R.id.player_controls_ui_text_time_current)
    TextView currentTimeTextView;

    @BindView(R.id.player_controls_ui_text_time_total_or_remaining)
    TextView totalOrRemainingTimeTextView;

    public PlayerControlsUi(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_player_controls, this);
        ButterKnife.bind(this);
    }

    public void setCallback(final Callback callback) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                callback.onSeekBarProgressChanged(seekBar, progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                callback.onSeekBarStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                callback.onSeekBarStopTrackingTouch(seekBar);
            }
        });

        skipToStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickSkipToStart();
            }
        });

        jumpBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickJumpBack();
            }
        });

        playPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickJumpPlayPause();
            }
        });

        jumpAheadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickJumpAhead();
            }
        });

        skipToEndButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickSkipToEnd();
            }
        });
    }

    public void bind(ViewModel viewModel) {
        currentTimeTextView.setText(viewModel.currentTime);
        totalOrRemainingTimeTextView.setText(viewModel.totalOrRemainingTime);
        seekBar.setProgress(viewModel.seekBarProgress);
    }

    static class ViewModel {

        final String currentTime;
        final String totalOrRemainingTime;
        final int seekBarProgress;

        ViewModel(String currentTime, String totalOrRemainingTime, int seekBarProgress) {
            this.currentTime = currentTime;
            this.totalOrRemainingTime = totalOrRemainingTime;
            this.seekBarProgress = seekBarProgress;
        }

    }

    public interface Callback {

        void onClickSkipToStart();

        void onClickJumpBack();

        void onClickJumpPlayPause();

        void onClickJumpAhead();

        void onClickSkipToEnd();

        void onSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onSeekBarStartTrackingTouch(SeekBar seekBar);

        void onSeekBarStopTrackingTouch(SeekBar seekBar);

    }

}
