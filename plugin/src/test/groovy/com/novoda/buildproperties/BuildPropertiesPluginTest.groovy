package com.novoda.buildproperties

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat
import static org.junit.Assert.fail

public class BuildPropertiesPluginTest {

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder()

    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder()
                .withProjectDir(temp.newFolder())
                .build()
    }

    @Test
    public void shouldNotFailBuildWhenDefiningPropertiesFromNonExistentFile() {
        project.apply plugin: BuildPropertiesPlugin
        project.buildProperties {
            foo {
                file project.file('foo.properties')
            }
        }
    }

    @Test
    public void shouldFailBuildWhenAccessingPropertyFromNonExistentFile() {
        project.apply plugin: BuildPropertiesPlugin
        project.buildProperties {
            foo {
                file project.file('foo.properties')
            }
        }

        try {
            project.buildProperties.foo['any'].string
            fail('Gradle exception not thrown')
        } catch (GradleException e) {
            assertThat(e.getMessage()).endsWith('foo.properties does not exist.')
        }
    }

    @Test
    public void shouldProvideSpecifiedErrorMessageWhenAccessingPropertyFromNonExistentFile() {
        project.apply plugin: BuildPropertiesPlugin

        def errorMessage = 'This file should contain the following properties:\n- foo\n- bar'
        try {
            project.buildProperties {
                foo {
                    file project.file('foo.properties'), errorMessage
                }
            }
            project.buildProperties.foo['any'].string
            fail('Gradle exception not thrown')
        } catch (GradleException e) {
            String message = e.getMessage()
            assertThat(message).contains('foo.properties does not exist.')
            assertThat(message).endsWith(errorMessage)
        }
    }

}
