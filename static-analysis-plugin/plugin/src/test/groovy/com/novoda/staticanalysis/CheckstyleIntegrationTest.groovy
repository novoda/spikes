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
public class CheckstyleIntegrationTest {

    private static final String EMPTY_MODULES = '''<!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.3//EN" "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">
<module name="Checker"/>
'''

    @Parameterized.Parameters
    public static List<Object[]> rules() {
        return [TestProjectRule.forJavaProject(), TestProjectRule.forAndroidProject()].collect { [it] as Object[] }
    }

    @Rule
    public final TestProjectRule projectRule

    public CheckstyleIntegrationTest(TestProjectRule projectRule) {
        this.projectRule = projectRule
    }

    @Test
    public void shouldFailBuildWhenCheckstyleWarningsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Checkstyle.SOURCES_WITH_WARNINGS)
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(0, 1)
        assertThat(result.logs).containsCheckstyleViolations(0, 1,
                result.buildFile('reports/checkstyle/main.html'))
    }

    @Test
    public void shouldFailBuildWhenCheckstyleErrorsOverTheThreshold() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Checkstyle.SOURCES_WITH_WARNINGS)
                .withSourceSet('test', Fixtures.Checkstyle.SOURCES_WITH_ERRORS)
                .withPenalty('''{
                    maxWarnings 100
                    maxErrors 0
                }''')
                .buildAndFail('check')

        assertThat(result.logs).containsLimitExceeded(1, 0)
        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsOrErrorsEncounteredAndNoThresholdTrespassed() {
        TestProject.Result result = projectRule.newProject()
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).doesNotContainCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleWarningsAndErrorsEncounteredAndNoThresholdTrespassed() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Checkstyle.SOURCES_WITH_WARNINGS)
                .withSourceSet('test', Fixtures.Checkstyle.SOURCES_WITH_ERRORS)
                .withPenalty('''{
                    maxWarnings 100
                    maxErrors 100
                }''')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).containsCheckstyleViolations(1, 1,
                result.buildFile('reports/checkstyle/main.html'),
                result.buildFile('reports/checkstyle/test.html'))
    }

    @Test
    public void shouldNotFailBuildWhenNoCheckstyleWarningsOrErrorsEncounteredAccordingToCustomModules() {
        TestProject.Result result = projectRule.newProject()
                .withSourceSet('main', Fixtures.Checkstyle.SOURCES_WITH_WARNINGS)
                .withSourceSet('test', Fixtures.Checkstyle.SOURCES_WITH_ERRORS)
                .withFile(EMPTY_MODULES, 'checkstyle.xml')
                .withPenalty('''{
                    maxWarnings 0
                    maxErrors 0
                }''')
                .withCheckstyle('''checkstyle {
                    configFile project.file('checkstyle.xml')
                }''')
                .build('check')

        assertThat(result.logs).doesNotContainLimitExceeded()
        assertThat(result.logs).doesNotContainCheckstyleViolations()
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleConfiguredToNotIgnoreFailures() {
        projectRule.newProject()
                .withSourceSet('main', Fixtures.Checkstyle.SOURCES_WITH_WARNINGS)
                .withSourceSet('test', Fixtures.Checkstyle.SOURCES_WITH_ERRORS)
                .withFile(Fixtures.Checkstyle.MODULES, 'config/checkstyle/checkstyle.xml')
                .withPenalty('''{
                    maxWarnings 1
                    maxErrors 1
                }''')
                .withCheckstyle('''checkstyle {
                    ignoreFailures false
                }''')
                .build('check')
    }

}
