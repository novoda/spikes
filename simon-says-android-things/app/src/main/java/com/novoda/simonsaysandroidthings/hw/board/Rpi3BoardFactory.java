package com.novoda.simonsaysandroidthings.hw.board;

class Rpi3BoardFactory extends BoardFactory {

    @Override
    public String getGreenLedGpio() {
        return "BCM5";
    }

    @Override
    public String getRedLedGpio() {
        return "BCM12";
    }

    @Override
    public String getBlueLedGpio() {
        return "BCM18";
    }

    @Override
    public String getYellowLedGpio() {
        return "BCM17";
    }

    @Override
    public String getGreenButtonGpio() {
        return "BCM19";
    }

    @Override
    public String getRedButtonGpio() {
        return "BCM20";
    }

    @Override
    public String getBlueButtonGpio() {
        return "BCM21";
    }

    @Override
    public String getYellowButtonGpio() {
        return "BCM27";
    }

    @Override
    public String getBuzzerPwm() {
        return "PWM1";
    }

    @Override
    public String getToggleGpio() {
        return "BCM25";
    }

}
