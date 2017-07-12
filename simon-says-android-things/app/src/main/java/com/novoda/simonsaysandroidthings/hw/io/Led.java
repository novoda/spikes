package com.novoda.simonsaysandroidthings.hw.io;

public interface Led extends AutoCloseable {

    void turnOn();

    void turnOff();

}
