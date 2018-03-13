package com.novoda.releaseplugin

import com.google.common.io.Resources
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
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
        buildFile = testProjectDir.newFile("build.gradle")
    }

    @Test
    void shouldPrintChangelog() throws IOException {
        configureExtensionWith('0.1')

        BuildResult build = buildWithArgs('printReleaseChangelog')

        assertThat(build.output).contains("- Initial release.")
    }

    private BuildResult buildWithArgs(String args) {
        GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withPluginClasspath()
                .withArguments(args)
                .build()
    }

    private void configureExtensionWith(String version) {
        def changelog = Resources.getResource("CHANGELOG.md").getPath()
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
                     changelog = '${changelog}'
                     version = '$version'
                }
                
                dependencies {
                    compile gradleApi()
                }
                """
        writeFile(buildFile, buildFileContent)
    }

    private static void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null
        try {
            output = new BufferedWriter(new FileWriter(destination))
            output.write(content)
        } finally {
            if (output != null) {
                output.close()
            }
        }
    }


}
