package com.novoda.buildproperties

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat

class EnvironmentPropertiesEntriesTest {

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder()

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables()

    private Project project
    private EnvironmentPropertiesEntries entries

    @Before
    public void setUp() {
        project = ProjectBuilder.builder()
                .withProjectDir(temp.newFolder())
                .build()
        entries = new EnvironmentPropertiesEntries(project)
    }

    @Test
    public void shouldNotContainUndefinedEnvironmentVariable() {
        assertThat(entries.contains('NOT_THERE')).isFalse()
    }

    @Test
    public void shouldThrowWhenEvaluatingUndefinedEnvironmentVariable() {
        Entry entry = entries['NOT_THERE']

        EntrySubject.assertThat(entry).willThrow(IllegalArgumentException)
    }

    @Test
    public void shouldContainDefinedEnvironmentVariable() {
        environmentVariables.set('FOO', 'foo')

        assertThat(entries.contains('FOO')).isTrue()
    }

    @Test
    public void shouldContainedProvidedValueForDefinedEnvironmentVariable() {
        environmentVariables.set('FOO', 'foo')

        Entry entry = entries['FOO']

        EntrySubject.assertThat(entry).hasValue('foo')
    }

    @Test
    public void shouldSupportEnvironmentKeysListing() {
        environmentVariables.set('FOO', 'foo')
        environmentVariables.set('BAR', 'bar')

        Enumeration<String> keys = entries.keys

        assertThat(Collections.list(keys)).containsAllOf('FOO', 'BAR')
    }

    @Test
    public void shouldProvideProjectRootDirAsParentFile() {
        File parentFile = entries.parentFile

        assertThat(parentFile).isEqualTo(project.rootDir)
    }

}
