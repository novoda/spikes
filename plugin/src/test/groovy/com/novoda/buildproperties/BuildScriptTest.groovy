package com.novoda.buildproperties

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat
import static junit.framework.Assert.fail

public class BuildScriptTest {

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
  public void shouldNotApplyPluginWhenAndroidPluginNotApplied() {
    try {
      project.apply plugin: BuildPropertiesPlugin
      fail('Gradle exception not thrown')
    } catch (GradleException e) {
      assertThat(e.getCause().getMessage()).isEqualTo('The build-properties plugin can be applied only after the Android plugin')
    }
  }

  @Test
  public void shouldApplyPluginWhenAndroidApplicationPluginApplied() {
    project.apply plugin: 'com.android.application'

    project.apply plugin: BuildPropertiesPlugin

    assertThat(project.plugins.hasPlugin(BuildPropertiesPlugin)).isTrue()
  }

  @Test
  public void shouldApplyPluginWhenAndroidLibraryPluginApplied() {
    project.apply plugin: 'com.android.library'

    project.apply plugin: BuildPropertiesPlugin

    assertThat(project.plugins.hasPlugin(BuildPropertiesPlugin)).isTrue()
  }

  @Test
  public void shouldFailBuildWhenPropertiesFileDoesNotExist() {
    project.apply plugin: 'com.android.library'
    project.apply plugin: BuildPropertiesPlugin

    try {
      project.buildProperties {
        foo {
          file project.file('foo.properties')
        }
      }
      fail('Gradle exception not thrown')
    } catch (GradleException e) {
      assertThat(e.getMessage()).endsWith('foo.properties does not exist.')
    }
  }

  @Test
  public void shouldProvideErrorMessageWhenPropertiesFileDoesNotExist() {
    project.apply plugin: 'com.android.library'
    project.apply plugin: BuildPropertiesPlugin

    def errorMessage = 'This file should contain the following properties:\n- foo\n- bar'
    try {
      project.buildProperties {
        foo {
          file project.file('foo.properties'), errorMessage
        }
      }
      fail('Gradle exception not thrown')
    } catch (GradleException e) {
      String message = e.getMessage()
      assertThat(message).contains('foo.properties does not exist.')
      assertThat(message).endsWith(errorMessage)
    }
  }

}
