package com.novoda.dungeoncrawler;

class FakeJoystickActuator implements JoystickActuator {
    @Override
    public JoyState getInput() {
        return new JoyState();
    }
}
