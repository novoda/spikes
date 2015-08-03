package com.ataulm.basic;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FooTest {

    private static final int HALF = 1;
    private static final int WHOLE = 2;

    @Mock
    Bar mockBar;

    Foo foo;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockBar.add(HALF, HALF)).thenReturn(WHOLE);
        foo = new Foo(mockBar);
    }

    @Test
    public void twiceWillReturnDoubleTheInput() {
        int actual = foo.twice(HALF);

        int expected = WHOLE;
        assertThat(actual).isEqualTo(expected);
    }

}
