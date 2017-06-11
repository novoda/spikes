package com.novoda.pianohero;

import java.util.List;

class AndroidThingThings {

    private final List<AndroidThing> androidThings;

    AndroidThingThings(List<AndroidThing> androidThings) {
        this.androidThings = androidThings;
    }

    public void open() {
        for (AndroidThing androidThing : androidThings) {
            androidThing.open();
        }
    }

    public void close() {
        for (AndroidThing androidThing : androidThings) {
            androidThing.close();
        }
    }

}
