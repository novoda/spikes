package com.novoda.staticanalysis

import com.novoda.test.Fixtures
import com.novoda.test.TestProject
import com.novoda.test.TestProjectRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.novoda.test.LogsSubject.assertThat

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

        assertThat(result.logs).doesNotContainCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildByDefaultWhenCheckstyleWarningsEncountered() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .build('check')

        assertThat(result.logs).containsCheckstyleViolations(0, 1,
                result.buildFile('reports/checkstyle/main.html'))
    }

    @Test
    public void shouldFailBuildByDefaultWhenCheckstyleErrorsEncountered() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .buildAndFail('check')

        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndNoPenalty() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('none')
                .build('check')

        assertThat(result.logs).doesNotContainCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndNoPenalty() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty('none')
                .build('check')

        assertThat(result.logs).containsCheckstyleViolations(0, 1,
                result.buildFile('reports/checkstyle/main.html'))
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorsEncounteredAndNoPenalty() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('none')
                .build('check')

        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('failOnErrors')
                .build('check')

        assertThat(result.logs).doesNotContainCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnErrors() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty('failOnErrors')
                .build('check')

        assertThat(result.logs).containsCheckstyleViolations(0, 1,
                result.buildFile('reports/checkstyle/main.html'))
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnErrors() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('failOnErrors')
                .buildAndFail('check')

        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('failOnWarnings')
                .build('check')

        assertThat(result.logs).doesNotContainCheckstyleViolations()
    }

    @Test
    public void shouldFailBuildWhenCheckstyleWarningsEncounteredAndPenaltyOnWarnings() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withPenalty('failOnWarnings')
                .buildAndFail('check')

        assertThat(result.logs).containsCheckstyleViolations(0, 1,
                result.buildFile('reports/checkstyle/main.html'))
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsEncounteredAndPenaltyOnWarnings() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('failOnWarnings')
                .buildAndFail('check')

        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorsEncounteredAndThresholdsNotReached() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS)
                .withSourceSet('test', Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS)
                .withPenalty('''{
        maxWarnings 1
        maxErrors 1
                }''')
                .build('check')

        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }
}
