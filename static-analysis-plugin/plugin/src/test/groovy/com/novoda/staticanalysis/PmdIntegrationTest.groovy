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

    private String pmd(String rules) {
        """pmd {
            ruleSetFiles = $rules
        }"""
    }
}
