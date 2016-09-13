package com.novoda.monkey

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

public class MonkeyConfigurationPluginTest {

    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.android.application'
    }

    @Test(expected = GradleException.class)
    public void givenAndroidPluginNotApplied_whenApplyingMonkey_thenItThrowsException() {
        project = ProjectBuilder.builder().build()

        project.apply plugin: MonkeyConfigurationPlugin
    }

    @Test
    public void givenAndroidPluginApplied_whenApplyingMonkey_thenItDoesNotThrowException() {
        project.apply plugin: MonkeyConfigurationPlugin
    }
}
