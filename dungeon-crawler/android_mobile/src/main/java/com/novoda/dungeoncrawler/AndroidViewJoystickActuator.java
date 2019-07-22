package com.novoda.dungeoncrawler;

class AndroidViewJoystickActuator implements JoystickActuator {

    private static final int MOVING_BACKWARD_ANGLE = -45;
    private static final int MOVING_FORWARD_ANGLE = 45;
    private static final int ATTACK_WOBBLE = 30000 + 1;

    private final JoyState joyState = new JoyState();
    private final JoystickView joystickView;

    AndroidViewJoystickActuator(JoystickView joystickView) {
        this.joystickView = joystickView;
    }

    @Override
    public JoyState getInput() {
        if (joystickView.isMovingBackward()) {
            joyState.tilt = MOVING_BACKWARD_ANGLE;
            joyState.wobble = 0;

        } else if (joystickView.isMovingForward()) {
            joyState.tilt = MOVING_FORWARD_ANGLE;
            joyState.wobble = 0;

        } else if (joystickView.isAttacking()) {
            joyState.tilt = 0;
            joyState.wobble = ATTACK_WOBBLE;
        } else {
            joyState.tilt = 0;
            joyState.wobble = 0;
        }
        return joyState;
    }
}
