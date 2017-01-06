package com.novoda.androidskeleton;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class FooTest {

    @Test
    public void getReturns90() {
        assertThat(new Foo().get()).isEqualTo(90);
    }

}