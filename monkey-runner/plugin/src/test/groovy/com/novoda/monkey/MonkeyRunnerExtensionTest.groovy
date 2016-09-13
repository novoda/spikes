package com.novoda.monkey

import org.junit.Before
import org.junit.Test

class MonkeyRunnerExtensionTest {

    private MonkeyRunnerExtension extension

    @Before
    public void setUp() {
        extension = new MonkeyRunnerExtension()
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNoTaskDependencySpecified_thenItThrows() {
        extension.packageNameFilter = 'com.package'

        extension.ensureMandatoryPropertiesPresent()
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNoPackageNameFilterSpecified_thenItThrows() {
        extension.taskDependency = 'someTask'

        extension.ensureMandatoryPropertiesPresent()
    }
}
