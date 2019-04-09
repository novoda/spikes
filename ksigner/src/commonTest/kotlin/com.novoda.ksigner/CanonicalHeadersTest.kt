package com.novoda.ksigner

import kotlin.test.Test
import kotlin.test.assertEquals

class CanonicalHeadersTest {

    @Test
    fun `build canonicalized headers`() {
        val headers = CanonicalHeaders.Builder()
            .add("test", "one")
            .add("test", "two")
            .add("hello", "world")
            .build()

        assertEquals("hello;test", headers.names)
        assertEquals("hello:world\ntest:one,two\n", headers.canonicalizedHeaders)
    }

    @Test
    fun `given header has value then return get first value`() {
        val headers = CanonicalHeaders.Builder()
            .add("test", "one")
            .add("test", "two")
            .add("hello", "world")
            .build()

        assertEquals("one", headers.getFirstValue("test"))
    }

    @Test
    fun `given header has no value then return null`() {
        val headers = CanonicalHeaders.Builder()
            .add("test", "one")
            .add("test", "two")
            .add("hello", "world")
            .build()

        assertEquals(null, headers.getFirstValue("does not exist"))
    }
}
