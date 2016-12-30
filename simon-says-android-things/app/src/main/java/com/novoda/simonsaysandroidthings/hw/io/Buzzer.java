package com.novoda.simonsaysandroidthings.hw.io;

public interface Buzzer extends AutoCloseable {

    void play(int frequencyHz);

    void stop();

}
