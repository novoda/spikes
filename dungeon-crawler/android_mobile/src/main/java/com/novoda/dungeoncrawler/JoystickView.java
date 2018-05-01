package com.novoda.dungeoncrawler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class JoystickView extends FrameLayout {

    private enum Action {
        NONE, BACKWARD, ATTACK, FORWARD
    }

    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    private static final long ACTION_ATTACK_DELAY_MS = 1000;

    private Action userAction = Action.NONE;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.joystick, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.backward).setOnTouchListener(this::onBackward);
        findViewById(R.id.attack).setOnClickListener(this::onAttack);
        findViewById(R.id.forward).setOnTouchListener(this::onForward);
    }

    private void onAttack(View v) {
        userAction = Action.ATTACK;
        delayResetAction(ACTION_ATTACK_DELAY_MS);
    }

    private void delayResetAction(long actionResetDelayMs) {
        MAIN_THREAD.removeCallbacks(resetLastAction);
        MAIN_THREAD.postDelayed(resetLastAction, actionResetDelayMs);
    }

    private final Runnable resetLastAction = () -> userAction = Action.NONE;

    private boolean onBackward(View v, MotionEvent motionEvent) {
        updateActionWith(motionEvent, Action.BACKWARD);
        return true;
    }

    private boolean onForward(View v, MotionEvent motionEvent) {
        updateActionWith(motionEvent, Action.FORWARD);
        return true;
    }

    private void updateActionWith(MotionEvent motionEvent, Action thisAction) {
        int action = motionEvent.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            userAction = thisAction;
        } else if (action == MotionEvent.ACTION_UP) {
            userAction = Action.NONE;
        }
    }

    public boolean isMovingBackward() {
        return userAction == Action.BACKWARD;
    }

    public boolean isAttacking() {
        return userAction == Action.ATTACK;
    }

    public boolean isMovingForward() {
        return userAction == Action.FORWARD;
    }
}
