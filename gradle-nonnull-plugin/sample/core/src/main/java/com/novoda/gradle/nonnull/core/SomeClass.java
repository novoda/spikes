package com.novoda.gradle.nonnull.core;

public class SomeClass {

    public SomeClass() {
        setValue(null);
    }

    // this should result in a warning in the IDEA
    private void setValue(Object value){
        //no-op
    }

}
