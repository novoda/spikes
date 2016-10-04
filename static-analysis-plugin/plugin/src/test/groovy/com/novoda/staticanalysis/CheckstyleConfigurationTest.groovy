package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.novoda.staticanalysis.BuildResultSubject.assertThat

@RunWith(Parameterized.class)
public class CheckstyleConfigurationTest {

    @Parameterized.Parameters
    public static List<Object[]> rules() {
        return [TestProjectRule.forJavaProject()].collect { [it] as Object[] }
    }

    @Rule
    public final TestProjectRule project

    public CheckstyleConfigurationTest(TestProjectRule project) {
        this.project = project
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenNoCheckstyleWarningsEncountered() {
        String buildScript = project.newBuildScriptBuilder()
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenCheckstyleWarningsEncountered() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildByDefaultWhenCheckstyleErrorsEncountered() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .build()
        BuildResult result = project
                .withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndNoPenalty() {
        String buildScript = project.newBuildScriptBuilder()
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndNoPenalty() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorsEncounteredAndNoPenalty() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.NONE)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        String buildScript = project.newBuildScriptBuilder()
                .withPenalty(Penalty.FAIL_ON_ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.FAIL_ON_ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnErrors() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.FAIL_ON_ERRORS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        String buildScript = project.newBuildScriptBuilder()
                .withPenalty(Penalty.FAIL_ON_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty(Penalty.FAIL_ON_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnWarnings() {
        String buildScript = project.newBuildScriptBuilder()
                .withSrcDirs(Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty(Penalty.FAIL_ON_WARNINGS)
                .build()
        BuildResult result = project.withBuildScript(buildScript)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolations()
    }
}
