package com.novoda.buildproperties

import org.gradle.api.GradleException
import org.gradle.api.Project

class BuildProperties {

    private final String name
    private final Project project
    private Entries entries

    BuildProperties(String name, Project project) {
        this.name = name
        this.project = project
    }

    String getName() {
        name
    }

    void file(File file, String errorMessage = null) {
        entries(LazyEntries.from {
            if (!file.exists()) {
                throw new GradleException("File $file.name does not exist.${errorMessage ? "\n$errorMessage" : ''}")
            }
            FilePropertiesEntries.create(name, file)
        })
    }

    void entries(Entries entries) {
        this.entries = entries
    }

    File getParentFile() {
        entries.parentFile
    }

    Enumeration<String> getKeys() {
        entries.keys
    }

    Entry getAt(String key) {
        if (project.hasProperty(key)) {
            new Entry(key, { project[key] })
        } else {
            entries.getAt(key)
        }
    }

    private static class LazyEntries extends Entries {

        private final Closure<Entries> entriesProvider

        static LazyEntries from(Closure<Entries> entriesProvider) {
            new LazyEntries(entriesProvider)
        }

        private LazyEntries(Closure<Entries> entriesProvider) {
            this.entriesProvider = entriesProvider.memoize()
        }

        private Entries getEntries() {
            entriesProvider.call()
        }

        @Override
        boolean contains(String key) {
            entries.contains(key)
        }

        @Override
        protected Object getValueAt(String key) {
            entries.getValueAt(key)
        }

        @Override
        File getParentFile() {
            entries.getParentFile()
        }

        @Override
        Enumeration<String> getKeys() {
            entries.getKeys()
        }

    }

}
