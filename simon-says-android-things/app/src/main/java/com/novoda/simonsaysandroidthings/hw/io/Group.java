package com.novoda.simonsaysandroidthings.hw.io;

public class Group implements AutoCloseable {

    private final Led led;
    private final Button button;
    private final Buzzer buzzer;
    private final int frequency;

    public Group(Led led, Button button, Buzzer buzzer, int frequency) {
        this.led = led;
        this.button = button;
        this.buzzer = buzzer;
        this.frequency = frequency;
    }

    public void play() {
        buzzer.play(frequency);
        led.turnOn();
    }

    public void stop() {
        buzzer.stop();
        led.turnOff();
    }

    @Override
    public void close() throws Exception {
        button.close();
        led.close();
    }
}
