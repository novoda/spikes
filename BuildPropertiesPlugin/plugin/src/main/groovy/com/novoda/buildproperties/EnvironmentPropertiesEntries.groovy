package com.novoda.buildproperties

import org.gradle.api.Project

class EnvironmentPropertiesEntries extends Entries {

    private final Project project

    EnvironmentPropertiesEntries(Project project) {
        this.project = project
    }

    @Override
    boolean contains(String key) {
        System.getenv().containsKey(key)
    }

    @Override
    protected Object getValueAt(String key) {
        String envValue = System.getenv(key)
        if (envValue != null) {
            return envValue
        }
        throw new IllegalArgumentException("No environment variable defined for key '$key'")
    }

    @Override
    File getParentFile() {
        project.rootDir
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(System.getenv().keySet())
    }

}
