package com.novoda.simonsaysandroidthings.hw.board;

class Rpi3BoardFactory extends BoardFactory {

    @Override
    public String getGreenLedGpio() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getRedLedGpio() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getBlueLedGpio() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getYellowLedGpio() {
        return "BCM26";
    }

    @Override
    public String getGreenButtonGpio() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getRedButtonGpio() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getBlueButtonGpio() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getYellowButtonGpio() {
        return "BCM21";
    }

    @Override
    public String getBuzzerPwm() {
        return "PWM0";
    }

    @Override
    public String getToggleGpio() {
        throw new RuntimeException("Not implemented yet");
    }

}
