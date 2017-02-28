package com.novoda;

import java.io.Serializable;

public class Stuff implements Serializable {

    private final String name;

    public Stuff(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
