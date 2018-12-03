package com.novoda.redditvideos

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        val appContext = ApplicationProvider.getApplicationContext<App>()
        assertEquals("com.novoda.redditvideos", appContext.packageName)
    }
}
