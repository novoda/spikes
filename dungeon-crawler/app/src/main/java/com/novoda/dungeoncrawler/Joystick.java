package com.novoda.dungeoncrawler;

class Joystick {

    // JOYSTICK
    private static final int JOYSTICK_ORIENTATION = 1;     // 0, 1 or 2 to set the angle of the joystick
    private static final int JOYSTICK_DIRECTION = 1;     // 0/1 to flip joystick direction
    static final int ATTACK_THRESHOLD = 30000; // The threshold that triggers an attack // TODO DOESN'T BELONG HERE
    static final int DEADZONE = 5;     // Angle to ignore

    private static final JoyState JOY_STATE = new JoyState();

    // MPU
    private final MPU6050 accelGyro;

    private static final RunningMedian MPU_ANGLE_SAMPLES = new RunningMedian(5);
    private static final RunningMedian MPU_WOBBLE_SAMPLES = new RunningMedian(5);

    Joystick(MPU6050 accelGyro) {
        this.accelGyro = accelGyro;
    }

    void initialise() {
        accelGyro.initialize();
    }

    // ---------------------------------
// ----------- JOYSTICK ------------
// ---------------------------------
    JoyState getInput() {
        // This is responsible for the player movement speed and attacking.
        // You can replace it with anything you want that passes a -90>+90 value to tilt
        // and any value to wobble that is greater than ATTACK_THRESHOLD (defined at start)
        // For example you could use 3 momentery buttons:
        // if(digitalRead(leftButtonPinNumber) == HIGH) tilt = -90;
        // if(digitalRead(rightButtonPinNumber) == HIGH) tilt = 90;
        // if(digitalRead(attackButtonPinNumber) == HIGH) wobble = ATTACK_THRESHOLD;

        MPU6050.Motion motion = accelGyro.getMotion6();
        int a = (JOYSTICK_ORIENTATION == 0 ? motion.ax : (JOYSTICK_ORIENTATION == 1 ? motion.ay : motion.az)) / 166;
        int g = (JOYSTICK_ORIENTATION == 0 ? motion.gx : (JOYSTICK_ORIENTATION == 1 ? motion.gy : motion.gz));
        if (Math.abs(a) < DEADZONE) {
            a = 0;
        }
        if (a > 0) {
            a -= DEADZONE;
        }
        if (a < 0) {
            a += DEADZONE;
        }
        MPU_ANGLE_SAMPLES.add(a);
        MPU_WOBBLE_SAMPLES.add(g);

        JOY_STATE.tilt = MPU_ANGLE_SAMPLES.getMedian();
        if (JOYSTICK_DIRECTION == 1) {
            JOY_STATE.tilt = 0 - JOY_STATE.tilt;
        }
        JOY_STATE.wobble = Math.abs(MPU_WOBBLE_SAMPLES.getHighest());
        return JOY_STATE;
    }

    static class JoyState {
        int tilt = 0;              // Stores the angle of the joystick
        int wobble = 0;            // Stores the max amount of acceleration (wobble)
    }
}
