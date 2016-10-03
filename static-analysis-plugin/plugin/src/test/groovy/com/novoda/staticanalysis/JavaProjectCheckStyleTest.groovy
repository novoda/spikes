package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.Test

import static com.google.common.truth.Truth.assertThat


class JavaProjectCheckStyleTest {
    private static final String CHECKSTYLE_VIOLATIONS_LOG = '> Checkstyle rule violations were found.'

    @Rule
    public TestProjectRule project = new TestProjectRule()

    @Test
    public void shouldNotFailBuildByDefaultWhenNoCheckstyleWarningsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenCheckstyleWarningsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldFailBuildByDefaultWhenCheckstyleErrorsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .build()
        BuildResult result = project
                .withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result.output).contains(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndNoPenalty() {
        String buildScript = new BuildScriptBuilder()
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndNoPenalty() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorsEncounteredAndNoPenalty() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        String buildScript = new BuildScriptBuilder()
                .withPenalty(Penalty.ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnErrors() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result.output).contains(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        String buildScript = new BuildScriptBuilder()
                .withPenalty(Penalty.WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result.output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result.output).contains(CHECKSTYLE_VIOLATIONS_LOG)
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnWarnings() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result.output).contains(CHECKSTYLE_VIOLATIONS_LOG)
    }
}
