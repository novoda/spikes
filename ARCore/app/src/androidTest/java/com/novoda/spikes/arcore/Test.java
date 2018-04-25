package com.novoda.spikes.arcore;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class Test {

    private UiDevice mDevice;

    @Before
    public void before() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertThat(mDevice, notNullValue());

        // Start from the home screen
        mDevice.pressHome();

    }

    @org.junit.Test
    public void test() throws InterruptedException, UiObjectNotFoundException {
        openApp("com.novoda.spikes.arcore");

        allowPermissionsIfNeeded();

        UiObject2 titleText = waitForObject(By.text("ARCore"));
        titleText.click();

        UiObject cameraView = mDevice.findObject(new UiSelector().resourceId("com.novoda.spikes.arcore:id/surfaceView"));

        cameraView.click();

//        UiObject screenName = mDevice.findObject(new UiSelector() .resourceId(“screen”));
//        screenName.click();

//        UiObject screenName = mDevice.findObject(new UiSelector() .resourceId(“screen”));


        Thread.sleep(500);
    }

    private void openApp(String packageName) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private UiObject2 waitForObject(BySelector selector) throws InterruptedException {
        UiObject2 object = null;
        int timeout = 30000;
        int delay = 1000;
        long time = System.currentTimeMillis();
        while (object == null) {
            object = mDevice.findObject(selector);
            Thread.sleep(delay);
            if (System.currentTimeMillis() - timeout > time) {
                fail();
            }
        }
        return object;
    }


    private void allowPermissionsIfNeeded() throws InterruptedException {
        Thread.sleep(10000);
        if (Build.VERSION.SDK_INT >= 23) {
            UiObject allowPermissions = mDevice.findObject(new UiSelector().text("ALLOW"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    System.out.println("There is no permissions dialog to interact with ");
                }
            }
        }
    }


}