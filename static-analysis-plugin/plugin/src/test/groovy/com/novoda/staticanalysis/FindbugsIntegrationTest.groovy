package com.novoda.staticanalysis

import com.novoda.test.TestProject
import com.novoda.test.TestProjectRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.novoda.test.Fixtures.Findbugs.*
import static com.novoda.test.LogsSubject.assertThat
import static com.novoda.test.TestProjectSubject.assumeThat

@RunWith(Parameterized.class)
class FindbugsIntegrationTest {

    @Parameterized.Parameters
    public static List<Object[]> rules() {
        return [TestProjectRule.forJavaProject(), TestProjectRule.forAndroidProject()].collect { [it] as Object[] }
    }

    @Rule
    public final TestProjectRule projectRule

    FindbugsIntegrationTest(TestProjectRule projectRule) {
        this.projectRule = projectRule
    }

    @Test
    public void shouldFailBuildWhenFindbugsWarningsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('debug', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withPenalty('''{
                    maxErrors = 10
                    maxWarnings = 10
                }''')
                .withFindbugs('findbugs {}')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsFindbugsViolations(0, 2,
                result.buildFile('reports/findbugs/debug.html'))
    }

    @Test
    public void shouldFailBuildWhenFindbugsErrorsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('debug', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 0
                }''')
                .withFindbugs('findbugs {}')
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(1, 0)
        assertThat(result.logs).containsFindbugsViolations(1, 0,
                result.buildFile('reports/findbugs/debug.html'))
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
                .withSourceSet('debug', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('release', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 10
                    maxWarnings = 10
                }''')
                .withFindbugs('findbugs {}')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsFindbugsViolations(1, 2,
                result.buildFile('reports/findbugs/debug.html'),
                result.buildFile('reports/findbugs/release.html'))
    }

    @Test
    public void shouldNotFailBuildWhenFindbugsConfiguredToNotIgnoreFailures() {
        projectRule.newProject()
                .withSourceSet('debug', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('release', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 10
                    maxWarnings = 10
                }''')
                .withFindbugs('findbugs { ignoreFailures = false }')
                .build('check')
    }

    @Test
    public void shouldNotFailBuildWhenFindbugsNotConfigured() {
        projectRule.newProject()
                .withSourceSet('debug', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('release', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 0
                }''')
                .build('check')
    }

    @Test
    public void shouldNotFailBuildWhenFindbugsConfiguredToIgnoreFaultySourceSets() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('debug', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION)
                .withSourceSet('release', SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 0
                    maxWarnings = 10
                }''')
                .withFindbugs('findbugs { exclude "HighPriorityViolator.java" }')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsFindbugsViolations(0, 2,
                result.buildFile('reports/findbugs/debug.html'))
    }

    @Test
    public void shouldCollectDuplicatedFindbugsWarningsAndErrorsAcrossAndroidVariantsForSharedSourceSets() {
        TestProject project = projectRule.newProject()
        assumeThat(project).isAndroidProject()

        TestProject.Result result = project
                .withSourceSet('main', SOURCES_WITH_LOW_VIOLATION, SOURCES_WITH_MEDIUM_VIOLATION, SOURCES_WITH_HIGH_VIOLATION)
                .withPenalty('''{
                    maxErrors = 10
                    maxWarnings = 10
                }''')
                .withFindbugs('findbugs {}')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsFindbugsViolations(2, 4,
                result.buildFile('reports/findbugs/debug.html'),
                result.buildFile('reports/findbugs/release.html'))
    }

}
