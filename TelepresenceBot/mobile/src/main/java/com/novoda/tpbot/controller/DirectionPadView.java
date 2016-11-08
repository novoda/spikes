package com.novoda.tpbot.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.R;

public class DirectionPadView extends RelativeLayout {

    private OnDirectionPressedListener onDirectionPressedListener = OnDirectionPressedListener.NO_OP;

    public DirectionPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.merge_bot_controller, this, true);

        View up = Views.findById(this, R.id.controller_up_button);
        up.setOnTouchListener(onButtonTouchListener);
        View down = Views.findById(this, R.id.controller_down_button);
        down.setOnTouchListener(onButtonTouchListener);
        View left = Views.findById(this, R.id.controller_left_button);
        left.setOnTouchListener(onButtonTouchListener);
        View right = Views.findById(this, R.id.controller_right_button);
        right.setOnTouchListener(onButtonTouchListener);

        View fire = Views.findById(this, R.id.controller_lazers_button);
        fire.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onDirectionPressedListener.onLazersFired();
                }
                return false;
            }
        });
    }

    private final View.OnTouchListener onButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            BotDirection direction = directionOf(view);
            if (action == MotionEvent.ACTION_DOWN) {
                onDirectionPressedListener.onDirectionPressed(direction);
            } else if (action == MotionEvent.ACTION_UP) {
                onDirectionPressedListener.onDirectionReleased(direction);
            }
            return false;
        }

        private BotDirection directionOf(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.controller_up_button:
                    return BotDirection.FORWARD;
                case R.id.controller_down_button:
                    return BotDirection.BACKWARD;
                case R.id.controller_left_button:
                    return BotDirection.STEER_LEFT;
                case R.id.controller_right_button:
                    return BotDirection.STEER_RIGHT;
                default:
                    throw new IllegalStateException("Could not map view to Direction " + view);
            }
        }
    };

    public void setOnDirectionPressedListener(OnDirectionPressedListener l) {
        this.onDirectionPressedListener = l;
    }
}
