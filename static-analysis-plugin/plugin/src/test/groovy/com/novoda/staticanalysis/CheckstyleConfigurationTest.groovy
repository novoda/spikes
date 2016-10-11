package com.novoda.staticanalysis

import com.novoda.test.Fixtures
import com.novoda.test.TestProject
import com.novoda.test.TestProjectRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.novoda.test.ResultSubject.assertThat

@RunWith(Parameterized.class)
public class CheckstyleConfigurationTest {

    @Parameterized.Parameters
    public static List<Object[]> rules() {
        return [TestProjectRule.forJavaProject(), TestProjectRule.forAndroidProject()].collect { [it] as Object[] }
    }

    @Rule
    public final TestProjectRule projectRule

    public CheckstyleConfigurationTest(TestProjectRule projectRule) {
        this.projectRule = projectRule
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenNoCheckstyleWarningsEncountered() {
        TestProject.Result result = projectRule.newProject()
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenCheckstyleWarningsEncountered() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildByDefaultWhenCheckstyleErrorsEncountered() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndNoPenalty() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('none')
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndNoPenalty() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty('none')
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorsEncounteredAndNoPenalty() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('none')
                .build('check')

        assertThat(result).containsCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('failOnErrors')
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty('failOnErrors')
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnErrors() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('failOnErrors')
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolationsLog()
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('failOnWarnings')
                .build('check')

        assertThat(result).doesNotContainCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty('failOnWarnings')
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolationsLog()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnWarnings() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('failOnWarnings')
                .buildAndFail('check')

        assertThat(result).containsCheckstyleViolationsLog()
    }
}
