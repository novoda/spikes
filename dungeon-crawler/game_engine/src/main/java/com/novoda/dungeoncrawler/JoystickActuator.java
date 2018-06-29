package com.novoda.dungeoncrawler;

public interface JoystickActuator {

    int DEADZONE = 30;     // Angle to ignore

    JoyState getInput();

    class JoyState {
        public int tilt = 0;              // Stores the angle of the joystick
        public int wobble = 0;            // Stores the max amount of acceleration (wobble)

        @Override
        public String toString() {
            return "{tilt :" + tilt + "," + " wobble : " + wobble + "}";
        }
    }

}
