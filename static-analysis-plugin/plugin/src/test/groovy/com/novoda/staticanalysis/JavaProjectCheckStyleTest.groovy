package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.Test

import static com.google.common.truth.Truth.assertThat


class JavaProjectCheckStyleTest {

    @Rule
    public TestProjectRule project = new TestProjectRule()

    @Test
    public void shouldFailBuildByDefaultWhenCheckstyleErrorEncountered() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDir(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .build()
        BuildResult result = project
                .withBuildScript(buildScript)
                .withDefaultCheckstyleConfig()
                .buildAndFail('clean', 'check')

        assertThat(result.output).contains('> Checkstyle rule violations were found.')
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenCheckstyleWarningsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDir(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .withDefaultCheckstyleConfig()
                .build('clean', 'check')

        assertThat(result.output).doesNotContain('> Checkstyle rule violations were found.')
    }

}
