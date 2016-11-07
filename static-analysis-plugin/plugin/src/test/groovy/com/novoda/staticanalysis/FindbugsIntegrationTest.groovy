package com.novoda.staticanalysis

import com.novoda.test.TestProject
import com.novoda.test.TestProjectRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.novoda.test.Fixtures.Findbugs.SOURCES_WITH_HIGH_VIOLATION
import static com.novoda.test.Fixtures.Findbugs.SOURCES_WITH_LOW_VIOLATION
import static com.novoda.test.Fixtures.Findbugs.SOURCES_WITH_MEDIUM_VIOLATION
import static com.novoda.test.LogsSubject.assertThat

@RunWith(Parameterized.class)
class FindbugsIntegrationTest {

    @Parameterized.Parameters
    public static List<Object[]> rules() {
        return [TestProjectRule.forJavaProject()].collect { [it] as Object[] }
    }

    @Rule
    public final TestProjectRule projectRule

    FindbugsIntegrationTest(TestProjectRule projectRule) {
        this.projectRule = projectRule
    }

    @Test
    public void shouldFailBuildWhenFindbugsWarningsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 0
                }''')
                .withFindbugs('findbugs {}')
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(0, 2)
        assertThat(result.logs).containsFindbugsViolations(0, 2,
                result.buildFile('reports/findbugs/main.html'))
    }

    @Test
    public void shouldFailBuildWhenFindbugsErrorsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 0
                }''')
                .withFindbugs('findbugs {}')
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(1, 0)
        assertThat(result.logs).containsFindbugsViolations(1, 0,
                result.buildFile('reports/findbugs/main.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoFindbugsWarningsOrErrorsEncounteredAndNoThresholdTrespassed() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 0
                }''')
                .withFindbugs('findbugs {}')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).doesNotContainFindbugsViolations()
    }

    @Test
    public void shouldNotFailBuildWhenFindbugsWarningsAndErrorsEncounteredAndNoThresholdTrespassed() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('main2', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 100
                    maxWarnings = 100
                }''')
                .withFindbugs('findbugs {}')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsFindbugsViolations(1, 2,
                result.buildFile('reports/findbugs/main.html'),
                result.buildFile('reports/findbugs/main2.html'))
    }

    @Test
    public void shouldNotFailBuildWhenFindbugsConfiguredToNotIgnoreFailures() {
        projectRule.newProject()
                .withSourceSet('main', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('main2', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 100
                    maxWarnings = 100
                }''')
                .withFindbugs('findbugs { ignoreFailures = false }')
                .build('check')
    }

    @Test
    public void shouldNotFailBuildWhenFindbugsNotConfigured() {
        projectRule.newProject()
                .withSourceSet('main', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('main2', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 0
                }''')
                .build('check')
    }

}
