package com.novoda.easycustomtabs;

import com.novoda.notils.exception.DeveloperError;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class EasyCustomTabsTest {

    @Test(expected = DeveloperError.class)
    public void getInstanceThrowsDeveloperErrorIfNotInitialized() {
        EasyCustomTabs.getInstance();
    }

    @Test
    public void getInstanceReturnsIfInitialized() {
        EasyCustomTabs.initialize(Robolectric.application);

        assertThat(EasyCustomTabs.getInstance()).isInstanceOf(EasyCustomTabs.class);
    }

}
