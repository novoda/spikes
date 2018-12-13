package com.novoda.testsupport

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

fun <T> withDelay(delayInMillis: Long = 100, block: () -> T) : T = runBlocking {
    delay(delayInMillis)
    block()
}
