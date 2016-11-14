package com.novoda.buildproperties

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat

class BuildPropertiesTest {

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder()

    private Project project
    private BuildProperties buildProperties
    private Entries entries

    @Before
    public void setUp() {
        project = ProjectBuilder.builder()
                .withProjectDir(temp.newFolder())
                .build()
        buildProperties = new BuildProperties('name', project)
        entries = new TestEntries()
        buildProperties.entries(entries)
    }

    @Test
    public void shouldReturnSamePropertyValueInEntries() {
        def value = buildProperties['a'].string

        assertThat(value).isEqualTo(entries['a'].string)
    }

    @Test
    public void shouldReturnSameKeysInEntries() throws Exception {
        Collection<String> keys = Collections.list(buildProperties.keys)

        assertThat(keys).containsExactlyElementsIn(Collections.list(entries.keys))
    }

    @Test
    public void shouldReturnDifferentValueFromEntriesWhenPropertyValueOverriddenInProject() {
        project.ext.a = 'x'

        def value = buildProperties['a'].string

        assertThat(value).isNotEqualTo(entries['a'].string)
        assertThat(value).isEqualTo('x')
    }

    static class TestEntries extends Entries {

        private Map<String, ?> entries = [
                a: 'a',
                b: 'b',
                c: 'c'
        ]

        @Override
        boolean contains(String key) {
            entries.containsKey(key)
        }

        @Override
        protected Object getValueAt(String key) {
            entries[key]
        }

        @Override
        File getParentFile() {
            throw new UnsupportedOperationException()
        }

        @Override
        Enumeration<String> getKeys() {
            Collections.enumeration(entries.keySet())
        }
    }
}
