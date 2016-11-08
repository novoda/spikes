package com.novoda.tpbot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.controller.BotDirection;
import com.novoda.tpbot.controller.OnDirectionPressedListener;

public class BotControllerActivity extends AppCompatActivity {

    private OnDirectionPressedListener onDirectionPressed = OnDirectionPressedListener.NO_OP;
    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            BotDirection direction = directionOf(view);
            if (action == MotionEvent.ACTION_DOWN) {
                onDirectionPressed.onDirectionPressed(direction);
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                onDirectionPressed.onDirectionReleased(direction);
                return true;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_controller);

        View up = Views.findById(this, R.id.controller_up_button);
        up.setOnTouchListener(onTouchListener);
        View down = Views.findById(this, R.id.controller_down_button);
        down.setOnTouchListener(onTouchListener);
        View left = Views.findById(this, R.id.controller_left_button);
        left.setOnTouchListener(onTouchListener);
        View right = Views.findById(this, R.id.controller_right_button);
        right.setOnTouchListener(onTouchListener);
    }

}
