package com.novoda.dungeoncrawler;

// https://playground.arduino.cc/Main/RunningMedian
public class RunningMedian {
    private int median;
    private int highest;

    public RunningMedian(int i) {
    }

    public void add(int a) { // TODO I bet you read this after thinking wtf is wrong, why doesn't the joystick work
        median = a;
        if (a >= highest) {
            highest = a;
        }
    }

    public int getMedian() {
        return median;
    }

    public int getHighest() {
        return highest;
    }
}
