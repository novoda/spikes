package com.novoda.releaseplugin.internal

import com.google.common.io.Resources
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat

class ParseChangeLogTaskTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Project project

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder
                .builder()
                .withProjectDir(temporaryFolder.newFolder())
                .build()
    }

    @Test
    void 'should parse changelog V1'() {
        def task = project.task('parseReleaseChangeLog', type: ParseChangeLogTask)
        task.version.set('0.1')
        def releaseChangelog = temporaryFolder.newFile('v0-1-changelog.md')
        task.changelog.set(Resources.getResource('CHANGELOG.md').getPath())
        task.releaseChangelog = releaseChangelog

        project.tasks['parseReleaseChangeLog'].execute()

        assertThat(task.releaseChangelog.text).isEqualTo('- Initial release.')
    }

    @Test
    void 'should parse changelog V2'() {
        def task = project.task('parseReleaseChangeLog', type: ParseChangeLogTask)
        task.version.set('0.2')
        def releaseChangelog = temporaryFolder.newFile('v0-2-changelog.md')
        task.changelog.set(Resources.getResource('CHANGELOG.md').getPath())
        task.releaseChangelog = releaseChangelog

        project.tasks['parseReleaseChangeLog'].execute()

        assertThat(releaseChangelog.text).isEqualTo("""- Second Feature ([PR#2](https://github.com/novoda/gradle-plugin-release-plugin/pull/2))
- First Feature ([PR#1](https://github.com/novoda/gradle-plugin-release-plugin/pull/1))""")
    }
}
