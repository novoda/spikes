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
                .withSourceSet('main', Fixtures.Findbugs.SOURCES_WITH_LOW_VIOLATION, Fixtures.Findbugs.SOURCES_WITH_MEDIUM_VIOLATION)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withFindbugs('findbugs {}')
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(0, 2)
    }

}
