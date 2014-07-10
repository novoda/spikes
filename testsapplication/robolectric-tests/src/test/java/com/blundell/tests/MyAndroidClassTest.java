package com.blundell.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class MyAndroidClassTest {

    @Test
    public void testSomething() throws Exception {
        MyActivity activity = new MyActivity();

        ActivityController.of(activity).attach().create();

        assertThat(activity.findViewById(R.id.my_hello_text_view)).isVisible();
    }
}
