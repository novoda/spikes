package com.novoda.monkey

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class MonkeyConfigurationPluginTest {

    private Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test(expected = GradleException.class)
    void givenAndroidPluginNotApplied_whenApplyingMonkey_thenItThrowsException() {
        project.apply plugin: 'android-command'

        project.apply plugin: MonkeyConfigurationPlugin

    }

    @Test(expected = GradleException.class)
    void givenCommandPluginNotApplied_whenApplyingMonkey_thenItThrowsException() {
        project.apply plugin: 'com.android.application'

        project.apply plugin: MonkeyConfigurationPlugin

    }

    @Test
    void givenPluginsApplied_whenApplyingMonkey_thenItDoesNotThrowException() {
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'com.novoda.android-command'

        project.apply plugin: MonkeyConfigurationPlugin

    }
}
