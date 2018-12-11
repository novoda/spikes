package com.novoda.testsupport

fun waitFor(timeout: Int = 1_000, condition: () -> Boolean) {
    val start = System.currentTimeMillis()
    while (!condition()) {
        check(System.currentTimeMillis() - start < timeout) { "Timed out" }
    }
}
