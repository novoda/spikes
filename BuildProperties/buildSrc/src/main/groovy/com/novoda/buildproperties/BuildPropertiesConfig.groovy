package com.novoda.buildproperties

import org.gradle.api.Project

class BuildPropertiesConfig {
    final Project project
    private props

    BuildPropertiesConfig(Project project) {
        this.project = project
    }

    void setFile(String name) { 
        props = new BuildProperties(project, project.file(name))
    }

    int getInt(String key) {
        props.getInt(key)
    }
}
