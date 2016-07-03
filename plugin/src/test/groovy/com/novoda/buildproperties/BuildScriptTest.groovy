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

  private BuildPropertiesPlugin plugin

  @Before
  public void setUp() {
    plugin = new BuildPropertiesPlugin()
  }

  @Test
  public void shouldNotApplyPluginWhenAndroidPluginNotApplied() {
    Project project = ProjectBuilder.builder()
            .withProjectDir(temp.newFolder())
            .build()
    try {
      plugin.apply(project)
      fail('Gradle exception not thrown')
    } catch (GradleException e) {
      assertThat(e.getMessage()).isEqualTo('The build-properties plugin can be applied only to an Android project')
    }
  }

  @Test
  public void shouldApplyPluginWhenAndroidApplicationPluginApplied() {
    Project project = ProjectBuilder.builder()
            .withProjectDir(temp.newFolder())
            .build()

    project.apply plugin: 'com.android.application'
    plugin.apply(project)
  }

  @Test
  public void shouldApplyPluginWhenAndroidLibraryPluginApplied() {
    Project project = ProjectBuilder.builder()
            .withProjectDir(temp.newFolder())
            .build()

    project.apply plugin: 'com.android.library'
    plugin.apply(project)
  }

}
