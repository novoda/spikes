package com.novoda.releaseplugin

import com.google.common.io.Resources
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat

class PrintChangelogIntegrationTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File buildFile

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Test
    void shouldPrintChangelog() throws IOException {
        configureExtensionWith('0.1', Resources.getResource('CHANGELOG.md'))

        BuildResult build = buildWithArgs('printReleaseChangelog')

        assertThat(build.output).contains('- Initial release.')
    }

    @Test
    void shouldNotRunParseChangelogTwiceWhenVersionAndChangelogAreSame() {
        configureExtensionWith('0.1', Resources.getResource('CHANGELOG.md'))
        buildWithArgs('printReleaseChangelog')

        BuildResult build = buildWithArgs('printReleaseChangelog')

        def parseReleaseChangelog = build.task(':parseReleaseChangelog')
        assertThat(parseReleaseChangelog.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
        assertThat(build.output).contains('- Initial release.')
    }

    @Test
    void shouldRunParseChangelogTwiceWhenVersionDiffersButChangelogAreSame() {
        configureExtensionWith('0.2', Resources.getResource('CHANGELOG.md'))
        buildWithArgs('printReleaseChangelog')

        configureExtensionWith('0.1', Resources.getResource('CHANGELOG.md'))
        BuildResult build = buildWithArgs('printReleaseChangelog')

        def parseReleaseChangelog = build.task(':parseReleaseChangelog')
        assertThat(parseReleaseChangelog.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(build.output).contains('- Initial release.')
    }

    @Test
    void shouldRunParseChangelogTwiceWhenVersionIsSameButChangelogDiffers() {
        configureExtensionWith('0.1', Resources.getResource('CHANGELOG.md'))
        buildWithArgs('printReleaseChangelog')

        def differentChangelog = testProjectDir.newFile('DIFFERENT_CHANGELOG.md')
        differentChangelog.text = """[Version 0.1](Foo)
                                                 --------------------------
                                                 
                                                 - BAR."""

        configureExtensionWith('0.1', differentChangelog.toURI().toURL())
        BuildResult build = buildWithArgs('printReleaseChangelog')

        def parseReleaseChangelog = build.task(":parseReleaseChangelog")
        assertThat(parseReleaseChangelog.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(build.output).contains('- BAR.')
    }

    private BuildResult buildWithArgs(String args) {
        GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withPluginClasspath()
                .withArguments(args)
                .build()
    }

    private void configureExtensionWith(String version, URL changelog) {
        def changelogPath = changelog.getPath()
        String buildFileContent = """
                
                plugins {
                    id 'com.novoda.release-plugin'
                }
                
                apply plugin: 'groovy'
                apply plugin: 'java-gradle-plugin'
                
                repositories {
                    mavenCentral()
                }

                releasePlugin {               
                     changelog = '${changelogPath}'
                     version = '$version'
                }
                
                dependencies {
                    compile gradleApi()
                }
                """
        buildFile.text = buildFileContent
    }

}
