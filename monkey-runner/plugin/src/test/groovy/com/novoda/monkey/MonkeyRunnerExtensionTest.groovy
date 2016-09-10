package com.novoda.monkey

import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class MonkeyRunnerExtensionTest {
    private MonkeyConfigurationPlugin.MonkeyRunnerExtension extension

    @Before
    public void setUp() {
        extension = new MonkeyConfigurationPlugin.MonkeyRunnerExtension()
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

    @Test
    public void defaultEventsCountIs50000() {
        extension.setDefaultsForOptionalProperties()

        assertThat(extension.eventsCount).isEqualTo(50000)
    }

    @Test
    public void defaultLogFileNameIsMonkeyLog() {
        extension.setDefaultsForOptionalProperties()

        assertThat(extension.logFileName).isEqualTo('monkey.log')
    }
}
