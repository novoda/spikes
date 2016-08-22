package com.novoda.buildproperties

import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class CompositeExceptionTest {

    private static final Exception EXCEPTION_1 = new RuntimeException("exception 1")
    private static final Exception EXCEPTION_2 = new RuntimeException("exception 2")
    private static final Exception EXCEPTION_3 = new RuntimeException("exception 3")

    @Test
    public void shouldHaveNoCause() {
        CompositeException compositeException = CompositeException.from(EXCEPTION_1)

        Throwable cause = compositeException.cause

        assertThat(cause).isNull()
    }

    @Test
    public void shouldContainWrappedExceptionMessage() {
        CompositeException compositeException = CompositeException.from(EXCEPTION_1)

        String message = compositeException.message

        assertThat(message).contains(inOrder(EXCEPTION_1.message))
    }

    @Test
    public void shouldContainAddedExceptionMessage() {
        CompositeException compositeException = CompositeException.from(EXCEPTION_1).add(EXCEPTION_2)

        String message = compositeException.message

        assertThat(message).contains(inOrder(EXCEPTION_1.message, EXCEPTION_2.message))
    }

    @Test
    public void shouldContainAddedCompositeExceptionMessage() {
        CompositeException innerException = CompositeException.from(EXCEPTION_1).add(EXCEPTION_2)
        CompositeException compositeException = innerException.add(EXCEPTION_3)

        String message = compositeException.message

        assertThat(message).contains(inOrder(EXCEPTION_1.message, EXCEPTION_2.message, EXCEPTION_3.message))
    }

    private static String inOrder(String... messages) {
        messages.inject ('', { acc, val -> acc + "\n- $val"})
    }

}
