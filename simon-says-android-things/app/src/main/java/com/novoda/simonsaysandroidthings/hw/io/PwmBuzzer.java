package com.novoda.simonsaysandroidthings.hw.io;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

public class PwmBuzzer implements Buzzer {

    private final Pwm pwm;

    public static PwmBuzzer create(String pinName, PeripheralManagerService service) {
        try {
            Pwm pwm = service.openPwm(pinName);
            pwm.setPwmDutyCycle(50);
            return new PwmBuzzer(pwm);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create PwmBuzzer with pin " + pinName, e);
        }
    }

    PwmBuzzer(Pwm pwm) {
        this.pwm = pwm;
    }

    @Override
    public void play(int frequencyHz) {
        try {
            pwm.setEnabled(false);
            pwm.setPwmFrequencyHz(frequencyHz);
            pwm.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            pwm.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            pwm.setEnabled(false);
            pwm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
