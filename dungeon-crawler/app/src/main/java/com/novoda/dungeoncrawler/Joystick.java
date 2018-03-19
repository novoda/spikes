package com.novoda.dungeoncrawler;

interface Joystick {

    int DEADZONE = 5;     // Angle to ignore

    JoyState getInput();

    class JoyState {
        int tilt = 0;              // Stores the angle of the joystick
        int wobble = 0;            // Stores the max amount of acceleration (wobble)
    }

}
