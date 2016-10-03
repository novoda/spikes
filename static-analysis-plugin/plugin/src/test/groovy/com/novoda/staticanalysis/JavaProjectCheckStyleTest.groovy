package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.Test

import static com.novoda.staticanalysis.BuildResultSubject.assertThat

class JavaProjectCheckStyleTest {

    @Rule
    public TestProjectRule project = new TestProjectRule()

    @Test
    public void shouldNotFailBuildByDefaultWhenNoCheckstyleWarningsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenCheckstyleWarningsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildByDefaultWhenCheckstyleErrorsEncountered() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .build()
        BuildResult result = project
                .withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndNoPenalty() {
        String buildScript = new BuildScriptBuilder()
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndNoPenalty() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorsEncounteredAndNoPenalty() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        String buildScript = new BuildScriptBuilder()
                .withPenalty(Penalty.FAIL_ON_ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.FAIL_ON_ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnErrors() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.FAIL_ON_ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        String buildScript = new BuildScriptBuilder()
                .withPenalty(Penalty.FAIL_ON_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.FAIL_ON_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnWarnings() {
        String buildScript = new BuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.FAIL_ON_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }
}
