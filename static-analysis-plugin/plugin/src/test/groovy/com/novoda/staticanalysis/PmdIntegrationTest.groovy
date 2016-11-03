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
public class PmdIntegrationTest {

    @Parameterized.Parameters
    public static List<Object[]> rules() {
        return [TestProjectRule.forJavaProject()].collect { [it] as Object[] }
    }

    @Rule
    public final TestProjectRule projectRule

    public PmdIntegrationTest(TestProjectRule projectRule) {
        this.projectRule = projectRule
    }

    @Test
    public void shouldFailBuildWhenPmdWarningsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_3_VIOLATION)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')"))
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(0, 1)
        assertThat(result.logs).containsPmdViolations(0, 1,
                result.buildFile('reports/pmd/main.html'))
    }

    @Test
    public void shouldFailBuildWhenPmdErrorOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_1_VIOLATION)
                .withSourceSet('main2', Fixtures.Pmd.SOURCES_WITH_PRIORITY_2_VIOLATION)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')"))
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(2, 0)
        assertThat(result.logs).containsPmdViolations(2, 0,
                result.buildFile('reports/pmd/main.html'),
                result.buildFile('reports/pmd/main2.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoPmdWarningsOrErrorsEncounteredAndNoThresholdTrespassed() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')"))
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).doesNotContainPmdViolations()
    }

    @Test
    public void shouldNotFailBuildWhenPmdWarningsAndErrorsEncounteredAndNoThresholdTrespassed() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_1_VIOLATION)
                .withSourceSet('main2', Fixtures.Pmd.SOURCES_WITH_PRIORITY_2_VIOLATION)
                .withSourceSet('main3', Fixtures.Pmd.SOURCES_WITH_PRIORITY_3_VIOLATION)
                .withSourceSet('main4', Fixtures.Pmd.SOURCES_WITH_PRIORITY_4_VIOLATION)
                .withPenalty('''{
                    maxWarnings 100
                    maxErrors 100
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')"))
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsPmdViolations(2, 2,
                result.buildFile('reports/pmd/main.html'),
                result.buildFile('reports/pmd/main2.html'),
                result.buildFile('reports/pmd/main3.html'),
                result.buildFile('reports/pmd/main4.html'))
    }

    /**
     * We found out PMD sometimes ... the same violation twice, but with different priority.
     * The issue seems related to the customisation of a rule coming from one of the predefined rule sets.
     */
    @Test
    public void shouldTakeInAccountDuplicatedViolationsWithDifferentPriorities() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_5_VIOLATION)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')"))
                .buildAndFail('check')

        assertThat(result.logs).containsPmdViolations(1, 1,
                result.buildFile('reports/pmd/main.html'))
    }

    @Test
    public void shouldNotFailBuildWhenPmdConfiguredToNotIgnoreFailures() {
        projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_1_VIOLATION)
                .withSourceSet('main2', Fixtures.Pmd.SOURCES_WITH_PRIORITY_2_VIOLATION)
                .withSourceSet('main3', Fixtures.Pmd.SOURCES_WITH_PRIORITY_3_VIOLATION)
                .withSourceSet('main4', Fixtures.Pmd.SOURCES_WITH_PRIORITY_4_VIOLATION)
                .withPenalty('''{
                    maxWarnings 100
                    maxErrors 100
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')", "ignoreFailures = false"))
                .build('check')
    }

    @Test
    public void shouldNotFailBuildWhenPmdConfiguredToIgnoreFaultySourceSets() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_1_VIOLATION)
                .withSourceSet('main2', Fixtures.Pmd.SOURCES_WITH_PRIORITY_2_VIOLATION)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withPmd(pmd("project.files('${Fixtures.Pmd.RULES.path}')", "exclude 'Priority1Violator.java'", "exclude 'Priority2Violator.java'"))
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).doesNotContainPmdViolations()
    }

    @Test
    public void shouldNotFailBuildWhenPmdNotConfigured() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Pmd.SOURCES_WITH_PRIORITY_1_VIOLATION)
                .withSourceSet('main2', Fixtures.Pmd.SOURCES_WITH_PRIORITY_2_VIOLATION)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).doesNotContainPmdViolations()
    }

    private String pmd(String rules, String... configs) {
        """pmd {
            ruleSetFiles = $rules
            ${configs.join('\n\t\t\t')}
        }"""
    }
}
