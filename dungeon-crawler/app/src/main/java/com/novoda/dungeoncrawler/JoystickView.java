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
    private static final long ACTION_RESET_DELAY_MS = 20; // random value ¯\_(ツ)_/¯

    private Action lastAction = Action.NONE;

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

    private void onAttack(View _) {
        lastAction = Action.ATTACK;
        delayResetLastAction();
    }

    private boolean onBackward(View _, MotionEvent motionEvent) {
        updateActionWith(motionEvent, Action.BACKWARD);
        return true;
    }

    private boolean onForward(View _, MotionEvent motionEvent) {
        updateActionWith(motionEvent, Action.FORWARD);
        return true;
    }

    private void delayResetLastAction() {
        MAIN_THREAD.removeCallbacks(resetLastAction);
        MAIN_THREAD.postDelayed(resetLastAction, ACTION_RESET_DELAY_MS);
    }

    private final Runnable resetLastAction = () -> lastAction = Action.NONE;

    private void updateActionWith(MotionEvent motionEvent, Action thisAction) {
        int action = motionEvent.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            lastAction = thisAction;
        } else if (action == MotionEvent.ACTION_UP) {
            delayResetLastAction();
        }
    }

    public boolean isMovingBackward() {
        return lastAction == Action.BACKWARD;
    }

    public boolean isAttacking() {
        return lastAction == Action.ATTACK;
    }

    public boolean isMovingForward() {
        return lastAction == Action.FORWARD;
    }
}
