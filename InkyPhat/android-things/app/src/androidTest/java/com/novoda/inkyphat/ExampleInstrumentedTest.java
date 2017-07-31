package com.novoda.inkyphat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Log.d("TUT", "Density " + appContext.getResources().getDisplayMetrics().density);
        assertEquals("com.novoda.inkyphat", appContext.getPackageName());
    }

    @Test
    public void givenBitShifting() throws Exception {
        byte foo = 0b01011111;

        int result = foo & 0x1F;

        assertEquals(31, result);
    }

    @Test
    public void givenName() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();
        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_test_1);

        new ImageDrawer(InkyPhat.Orientation.LANDSCAPE).filterImage(bitmap);

    }
}
