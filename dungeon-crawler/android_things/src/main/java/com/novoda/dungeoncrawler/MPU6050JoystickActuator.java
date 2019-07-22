package com.novoda.dungeoncrawler;

class MPU6050JoystickActuator implements JoystickActuator {

    // JOYSTICK
    private static final int JOYSTICK_ORIENTATION = 1;     // 0, 1 or 2 to set the angle of the joystick
    private static final int JOYSTICK_DIRECTION = 1;     // 0/1 to flip joystick direction

    private static final JoyState JOY_STATE = new JoyState();

    // MPU
    private final MPU6050 accelGyro;

    private static final RunningMedian MPU_ANGLE_SAMPLES = new RunningMedian(5);
    private static final RunningMedian MPU_WOBBLE_SAMPLES = new RunningMedian(5);

    MPU6050JoystickActuator(MPU6050 accelGyro) {
        this.accelGyro = accelGyro;
        accelGyro.initialize();
    }

    @Override
    public JoyState getInput() {
        // This is responsible for the player movement speed and attacking.
        // You can replace it with anything you want that passes a -90>+90 value to tilt
        // and any value to wobble that is greater than ATTACK_THRESHOLD (defined at start)
        // For example you could use 3 momentery buttons:
        // if(digitalRead(leftButtonPinNumber) == HIGH) tilt = -90;
        // if(digitalRead(rightButtonPinNumber) == HIGH) tilt = 90;
        // if(digitalRead(attackButtonPinNumber) == HIGH) wobble = ATTACK_THRESHOLD;

        MPU6050.Motion motion = accelGyro.getMotion6();
//        float a = (JOYSTICK_ORIENTATION == 0 ? motion.ax : (JOYSTICK_ORIENTATION == 1 ? motion.ay : motion.az)) / 166;
//        float g = (JOYSTICK_ORIENTATION == 0 ? motion.gx : (JOYSTICK_ORIENTATION == 1 ? motion.gy : motion.gz));
        float a = motion.ay * 150 + 10;
        float g = motion.gy * 150;
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
            JOY_STATE.tilt = -JOY_STATE.tilt;
        }
        JOY_STATE.wobble = Math.abs(MPU_WOBBLE_SAMPLES.getHighest());
//        Log.d("TUT", "Return " + JOY_STATE);
        return JOY_STATE;
    }

}
