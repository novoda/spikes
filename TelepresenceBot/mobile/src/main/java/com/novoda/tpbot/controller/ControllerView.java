package com.novoda.tpbot.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.R;

public class ControllerView extends RelativeLayout {

    private ControllerListener controllerListener = ControllerListener.NO_OP;

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.merge_controller_view, this, true);

        View up = Views.findById(this, R.id.controller_up_button);
        up.setOnTouchListener(onButtonTouchListener);
        View down = Views.findById(this, R.id.controller_down_button);
        down.setOnTouchListener(onButtonTouchListener);
        View left = Views.findById(this, R.id.controller_left_button);
        left.setOnTouchListener(onButtonTouchListener);
        View right = Views.findById(this, R.id.controller_right_button);
        right.setOnTouchListener(onButtonTouchListener);

        View fire = Views.findById(this, R.id.controller_lazers_button);
        fire.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                controllerListener.onLazersPressed();
            }
        });
    }

    private final View.OnTouchListener onButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            Direction direction = directionOf(view);
            if (action == MotionEvent.ACTION_DOWN) {
                controllerListener.onDirectionPressed(direction);
            } else if (action == MotionEvent.ACTION_UP) {
                controllerListener.onDirectionReleased(direction);
            }
            return false;
        }

        private Direction directionOf(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.controller_up_button:
                    return Direction.FORWARD;
                case R.id.controller_down_button:
                    return Direction.BACKWARD;
                case R.id.controller_left_button:
                    return Direction.STEER_LEFT;
                case R.id.controller_right_button:
                    return Direction.STEER_RIGHT;
                default:
                    throw new IllegalArgumentException("View did not include any of the controller buttons");
            }
        }
    };

    public void setControllerListener(ControllerListener l) {
        this.controllerListener = l;
    }
}
