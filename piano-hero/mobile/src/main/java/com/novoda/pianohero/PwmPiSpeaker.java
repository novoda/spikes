package com.novoda.pianohero;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

public class PwmPiSpeaker implements AndroidThing, Speaker {

    private Pwm bus;

    @Override
    public void open() {
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            bus = service.openPwm("PWM1");
            bus.setPwmDutyCycle(50);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open buzzer bus");
        }
    }

    @Override
    public void start(double frequency) {
        try {
            bus.setPwmFrequencyHz(frequency);
            bus.setEnabled(true);
        } catch (IOException e) {
            throw new IllegalStateException("can't make noise", e);
        }
    }

    @Override
    public void stop() {
        try {
            bus.setEnabled(false);
        } catch (IOException e) {
            throw new IllegalStateException("can't stop noise", e);
        }
    }

    @Override
    public void close() {
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("!!!", "couldn't close buzzer bus", e);
        }
    }

}
