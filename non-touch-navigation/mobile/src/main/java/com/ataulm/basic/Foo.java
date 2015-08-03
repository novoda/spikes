package com.ataulm.basic;

public class Foo {

    private final Bar bar;

    Foo(Bar bar) {
        this.bar = bar;
    }

    int twice(int x) {
        return bar.add(x, x);
    }

}
