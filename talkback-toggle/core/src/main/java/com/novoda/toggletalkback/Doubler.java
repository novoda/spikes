package com.novoda.toggletalkback;

public class Doubler {

    private final Adder adder;

    Doubler(Adder adder) {
        this.adder = adder;
    }

    int twice(int x) {
        return adder.add(x, x);
    }

}
