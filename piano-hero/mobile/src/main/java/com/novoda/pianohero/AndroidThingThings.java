package com.novoda.pianohero;

import java.util.ArrayList;
import java.util.List;

class AndroidThingThings {

    private final List<AndroidThing> androidThings = new ArrayList<>();

    public void add(AndroidThing androidThing) {
        androidThings.add(androidThing);
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
