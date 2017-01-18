package com.novoda.gradle.nonnull.core;

public class SomeClass {

    public SomeClass() {

        // this should result in a warning in the IDEA
        setValue(null);
    }

    private void setValue(Object value){
        //no-op
    }

}
